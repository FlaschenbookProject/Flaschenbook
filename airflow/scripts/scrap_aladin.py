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


def scrap_review_and_content(isbn_list):
    reviews = []
    contents = []

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)
        context = browser.new_context(ignore_https_errors=True)
        page = context.new_page()

        for isbn in isbn_list:
            reviews_one_book = []
            contents_one_book = []

            search_url = f"https://www.aladin.co.kr/search/wsearchresult.aspx?SearchTarget=All&SearchWord={isbn}"

            try:
                page.goto(search_url)
                print(f"{isbn}으로 검색했습니다.")
            except Exception as e:
                print(f"Search {isbn} Error encountered: {e}")

            # XPath를 사용하여 링크 찾기
            detail_link_xpath = '//*[@id="Search3_Result"]//ul/li[2]/a[1][contains(@class, "bo3")]'
            detail_link_locator = page.locator(detail_link_xpath)

            if detail_link_locator:
                detail_link = detail_link_locator.get_attribute('href')
                if detail_link:
                    page.goto(detail_link)
                else:
                    print(f"{isbn} 링크를 찾을 수 없습니다.")
            else:
                # 링크가 존재하지 않는 경우 다음 isbn 검색으로 이동
                print(f"{isbn} 책 결과가 존재하지 않습니다.")
                return

            # 책 속에서 content
            base_xpath = '//*[@id="u3_{}_more"]'

            for i in range(1, 100):
                print(f"{isbn} 책 속에서 시작")
                book_xpath = base_xpath.format(i)
                print(book_xpath)
                more_button_xpath = '//*[@id="Underline3_more"]/a'  # "더 보기" 버튼의 XPath

                try:
                    page.evaluate('''(selector) => {
                        const element = document.querySelector(selector);
                        element.style.display = 'block';
                        element.scrollIntoView({ behavior: "auto", block: "nearest", inline: "nearest" });
                    }''', book_xpath + '/text()')

                    # 요소를 다시 선택
                    content_element = page.locator(book_xpath + '/text()')

                    if not content_element:
                        print(f"{isbn} 책 속으로가 없습니다.")

                    content_data = content_element.inner_text()  # 책 속으로 content

                    if content_data:
                        content_dict = {'isbn': isbn, 'content': content_data.strip()}
                        contents_one_book.append(content_dict)
                        print(f"{content_data}")
                    else:
                        print(f"{isbn}에 책 속으로가 더 이상 없습니다.")
                        break
                except Exception as e:
                    if "TimeoutError" in str(e):
                        print(f"TimeoutError: {isbn} 책 속으로가 없습니다.")
                        break
                    else:
                        print(f"An error occurred: {e}")
                        break

                more_button = page.locator(more_button_xpath).first()
                if more_button:
                    more_button.click()
                    page.wait_for_load_state('networkidle')

            print(f"{isbn} review 시작")
            # 리뷰 스크래핑
            total_xpath = '//*[@id="CommentReviewTab"]/div[1]/ul/li[2]'

            while not page.locator(total_xpath).first.is_visible():
                page.evaluate('window.scrollBy(0, 100);')  # 100 픽셀씩 스크롤
            page.locator(total_xpath).click()

            review_more_button = page.locator('//*[@id="divReviewPageMore"]/div[1]/a')
            while review_more_button.is_visible():
                # "더 보기" 버튼이 있으면 클릭
                review_more_button.click()
                page.evaluate('window.scrollBy(0, 100);')  # 100 픽셀씩 스크롤

            review_list_xpath = '//*[@id="CommentReviewList"]/div[1]/ul/div[contains(@class, "hundred_list")]'
            page.wait_for_selector(review_list_xpath)

            review_cnt = page.locator(review_list_xpath).count()
            print(review_cnt)
            for element in range(1, review_cnt * 2 + 1, 2):
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

                review_dict = {'isbn': isbn, 'web_code': WEBCODE, 'content': review_text, 'rating': rating, 'wrt_date': date}
                reviews_one_book.append(review_dict)

                print("리뷰:", review_text)
                print("평점:", rating)
                print("작성일:", date)
                print("----------------")

            if len(reviews_one_book) == 0 and len(contents_one_book) == 0:
                continue
            reviews.extend(reviews_one_book)
            contents.extend(contents_one_book)
        page.close()
        context.close()
        browser.close()
    return reviews, contents


def upload_to_s3(bucket_name, reviews, type):
    df = pd.DataFrame(reviews)
    fs = s3fs.S3FileSystem(anon=False)

    if type == "review":
        bucket_path = f"s3://{bucket_name}/curated/review/{DATE}/{BOOK_TYPE}_book_reviews.parquet"
    else:
        bucket_path = f"s3://{bucket_name}/curated/review/{DATE}/{BOOK_TYPE}_book_content.parquet"
    print(f"{bucket_path} 파일 업로드 시작")
    with fs.open(bucket_path, 'wb') as f:
        df.to_parquet(f)
    print(f"{bucket_path} 파일 업로드 완료!")


def main():
    load_dotenv()
    bucket_name = os.environ.get("BUCKET_NAME")
    isbn_object_key = f"raw/isbn/{DATE}/{BOOK_TYPE}.csv"
    isbn_list = get_isbn_list(bucket_name, isbn_object_key)
    reviews, contents = scrap_review_and_content(isbn_list)
    upload_to_s3(bucket_name, reviews, "review")
    upload_to_s3(bucket_name, contents, "content")


if __name__ == "__main__":
    main()
