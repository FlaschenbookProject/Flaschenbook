import os
from playwright.sync_api import sync_playwright
from utils.api_operations import get_isbn_list
from dotenv import load_dotenv
import s3fs
import pandas as pd
import nest_asyncio
nest_asyncio.apply()


DATE = "2023-08-19"
WEBCODE = "AL"
BOOK_TYPE = "best"


def scrap_review(isbn_list, bucket_name):
    reviews = []

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)
        context = browser.new_context(ignore_https_errors=True)
        page = context.new_page()

        num = 1
        for inx, isbn in enumerate(isbn_list):
            try:
                reviews_one_book = []

                search_url = f"https://www.aladin.co.kr/search/wsearchresult.aspx?SearchTarget=All&SearchWord={isbn}"

                try:
                    page.goto(search_url)
                    print(f"{isbn}으로 검색했습니다.")
                except Exception as e:
                    print(f"Search {isbn} Error encountered: {e}")
                    continue

                # XPath를 사용하여 성인 책인지 아닌지 확인하기
                image_xpath = '//*[@id="Search3_Result"]/div/table/tbody/tr/td[2]/table/tbody/tr[1]/td/div/a/img'
                try:
                    if page.locator(image_xpath).is_visible():
                        img_element = page.locator(image_xpath)
                        src_attribute = img_element.get_attribute('src')

                        if 'img/19book_' in src_attribute:
                            print(f"{isbn}은 성인 책입니다.")
                            continue
                except Exception as e:
                    print(f"Search {isbn} Error encountered: {e}")

                # XPath를 사용하여 링크 찾기
                detail_link_xpath = '//*[@id="Search3_Result"]//ul/li[2]/a[1][contains(@class, "bo3")]'
                try:
                    detail_link_locator = page.locator(detail_link_xpath).first
                except Exception as e:
                    print(f"Search {isbn} Error encountered: {e}")
                    continue

                try:
                    if detail_link_locator:
                        detail_link = detail_link_locator.get_attribute('href')
                        if detail_link:
                            page.goto(detail_link)
                        else:
                            print(f"{isbn} 링크를 찾을 수 없습니다.")
                    else:
                        # 링크가 존재하지 않는 경우 다음 isbn 검색으로 이동
                        print(f"{isbn} 책 결과가 존재하지 않습니다.")
                        continue
                except Exception as e:
                    print(e)
                    print(f"{isbn} 책 결과가 존재하지 않습니다.")
                    continue

                print(f"{isbn} review 시작")
                # 리뷰 스크래핑
                total_xpath = '//*[@id="CommentReviewTab"]/div[1]/ul/li[2]'

                while not page.locator(total_xpath).first.is_visible():
                    page.evaluate('window.scrollBy(0, 100);')  # 100 픽셀씩 스크롤
                    page.wait_for_load_state('domcontentloaded')
                page.locator(total_xpath).click()

                review_more_button = page.locator('//*[@id="divReviewPageMore"]/div[1]/a')
                prev_scroll_position = -1  # 이전 스크롤 위치

                while True:
                    try:
                        # 현재 스크롤 위치 가져오기
                        current_scroll_position = page.evaluate('window.scrollY')

                        if current_scroll_position == prev_scroll_position:
                            print("더 이상 스크롤이 내려가지 않음")
                            break
                        page.evaluate('window.scrollBy(0, 100);')  # 100 픽셀씩 스크롤
                        page.wait_for_load_state('domcontentloaded')
                        if review_more_button.is_visible():
                            review_more_button.click()

                        # 스크롤 위치가 더 이상 변하지 않으면 종료
                        prev_scroll_position = current_scroll_position  # 이전 스크롤 위치 업데이트
                    except Exception as e:
                        if "TimeoutError" in str(e):
                            print(f"Timeout Error: {e}")
                            continue
                        else:
                            print(f"An error occurred: {e}")
                            continue

                review_list_xpath = '//*[@id="CommentReviewList"]/div[1]/ul/div[contains(@class, "hundred_list")]'
                page.wait_for_selector(review_list_xpath)

                if page.locator(review_list_xpath).count() <= 0:
                    print(f"{isbn} 책의 리뷰가 없습니다")
                    continue

                review_cnt = page.locator(review_list_xpath).count()
                print(review_cnt)
                for element in range(1, review_cnt * 2 + 1, 2):
                    try:
                        base_xpath = f'//*[@id="CommentReviewList"]/div[1]/ul/div[{element}]'
                        date = page.inner_text(base_xpath + '/div[2]/div/ul/li[2]/div[1]/span[1]')
                        review_text = page.inner_text(base_xpath + '/div[2]/div/ul/li[1]/div/div/a[1]')
                        rating = 0

                        rating_xpath = base_xpath + '/div[1]/img[{}]'

                        for i in range(1, 6):
                            star_image = rating_xpath.format(i)
                            star_src = page.get_attribute(star_image, 'src')

                            if star_src == "//image.aladin.co.kr/img/shop/2018/icon_star_off.png":
                                break
                            else:
                                rating += 2

                        review_dict = {'isbn': isbn, 'web_code': WEBCODE, 'content': review_text, 'rating': float(rating), 'wrt_date': date}
                        reviews_one_book.append(review_dict)

                        print("리뷰:", review_text)
                        print("평점:", rating)
                        print("작성일:", date)
                        print("----------------")
                    except Exception as e:
                        if "TimeoutError" in str(e):
                            print(f"Timeout Error: {e}")
                            continue
                        else:
                            print(f"An error occurred: {e}")
                            continue

                if len(reviews_one_book) == 0:
                    continue
                reviews.extend(reviews_one_book)

            except Exception as e:
                print(f"스크래핑 중 오류 발생: {str(e)}")
                continue

            if inx % 100 == 0:
                upload_to_s3(bucket_name, reviews, num)
                num += 1
                reviews = []

        page.close()
        context.close()
        browser.close()


def upload_to_s3(bucket_name, reviews, num):
    df = pd.DataFrame(reviews)
    fs = s3fs.S3FileSystem(anon=False)
    bucket_path = f"s3://{bucket_name}/curated/review/{DATE}/{BOOK_TYPE}_book_reviews_{WEBCODE}_{num}.parquet"
    print(f"{bucket_path} 파일 업로드 시작")
    with fs.open(bucket_path, 'wb') as f:
        df.to_parquet(f)
    print(f"{bucket_path} 파일 업로드 완료!")


def main():
    load_dotenv()
    bucket_name = os.environ.get("BUCKET_NAME")
    isbn_object_key = f"raw/isbn/{DATE}/{BOOK_TYPE}.csv"
    isbn_list = get_isbn_list(bucket_name, isbn_object_key)
    scrap_review(isbn_list, bucket_name)


if __name__ == "__main__":
    main()
