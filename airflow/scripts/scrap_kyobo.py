from utils.api_operations import get_isbn_list
from dotenv import load_dotenv
import s3fs
import pandas as pd
import os
import re
from playwright.sync_api import sync_playwright
import sys
import nest_asyncio
nest_asyncio.apply()


def scrap_review_and_content(isbn_list, WEBCODE):
    reviews = []
    contents = []

    with sync_playwright() as p:
        browser = p.chromium.launch(headless=False)
        # SSL/TLS 에러 무시를 위해 Context 생성
        context = browser.new_context(ignore_https_errors=True)
        page = context.new_page()

        for i, isbn in enumerate(isbn_list):
            print(f"{i + 1}번째 {isbn} 스크래핑 시작")
            print("URL로 이동 중...")
            try:
                page.goto(
                    f"https://search.kyobobook.co.kr/search?keyword={isbn}&gbCode=TOT&target=total")
                print("URL에 성공적으로 접속했습니다.")
            except Exception as e:
                print(f"Error encountered: {e}")
                continue

            book_detail_url_xpath = 'xpath=//*[@id="shopData_list"]/ul/li/div[1]/div[2]/div[2]/div[1]/div/a'
            # 검색 결과 없으면 넘어감
            try:
                if not page.locator(book_detail_url_xpath).is_visible():
                    continue
                if page.locator('//*[@id="shopData_list"]/ul/li/div[1]/div[1]/a/span').get_attribute("class") == "img_box adult":
                    continue
                page.locator(book_detail_url_xpath).click()
                print("도서 상세페이지 진입 완료")
            except Exception as e:
                print(f"Error encountered: {e}")
                continue

            page.wait_for_load_state('domcontentloaded')

            book_content = ""
            try:
                print(f"{isbn} 책 속으로 조회 시작")
                if page.locator('.product_detail_area.book_inside').count() > 0:
                    # 책 속으로가 존재하는 경우에만 텍스트를 추출하고 출력
                    book_content = page.locator('.product_detail_area.book_inside .auto_overflow_inner .info_text').inner_text()
                    book_content = book_content.replace('<br>', '\n')
                else:
                    print(f"'{isbn}'의 책 속으로가 없습니다.")
            except Exception as e:
                print(f"Error encountered: {e}")
                continue

            if len(book_content) != 0:
                content_list = book_content.split('\n')
                for content in content_list:
                    if content:
                        content_dict = {'isbn': isbn, 'content': content}
                        contents.append(content_dict)
                        print(f"{isbn} 내용: {content}")

            try:
                review_cnt_text_xpath = '//*[@id="contents"]/div[2]/div[1]/div/div[1]/ul/li[3]/a/span/span'
                page.wait_for_selector(review_cnt_text_xpath)
                review_cnt_text = page.locator(
                    review_cnt_text_xpath).inner_text()

                match = re.search(r'\((\d+)\)', review_cnt_text)
                review_cnt = int(match.group(1)) if match else 0
                if not review_cnt:
                    print("리뷰가 없습니다!")
                    continue
            except Exception as e:
                print(f"Error encountered: {e}")
                continue

            page_num = 1
            review_cnt = 1
            next_button_xpath = '//*[@id="ReviewList1"]/div[3]/div[2]/div/div[2]/button[2]'

            print(f"{isbn} 리뷰 수집 시작")
            while True:
                review_list_xpath = "//*[@id='ReviewList1']/div[3]/div[2]/div/div[1]/div"
                page.wait_for_selector(review_list_xpath)
                reviews_per_page = len(page.locator(
                    review_list_xpath).element_handles())
                print(f"리뷰 페이지 : {page_num}")

                for i in range(1, reviews_per_page + 1):
                    review = {}
                    base_xpath = f"//*[@id='ReviewList1']/div[3]/div[2]/div/div[1]/div[{i}]"

                    # 블라인드 처리 댓글 예외처리
                    content_display_xpath = f'{base_xpath}/div[2]/div[1]'
                    content_display_style = page.locator(
                        content_display_xpath).get_attribute("style")

                    if content_display_style and "display: none" in content_display_style:
                        continue

                    # 펼치기 여부
                    more_button_xpath = f'{base_xpath}/div[3]/button'
                    content_overflow_xpath = f"{base_xpath}/div[2]/div/div[1]/div[1]/div"
                    content_normal_xpath = f"{base_xpath}/div[2]/div/div/div/div"

                    if page.locator(more_button_xpath).is_visible():
                        page.wait_for_selector(content_overflow_xpath)
                        content = page.locator(
                            content_overflow_xpath).inner_text()
                    else:
                        if page.locator(
                                content_normal_xpath).is_visible():
                            page.wait_for_selector(content_normal_xpath)
                            content = page.locator(
                                content_normal_xpath).inner_text()

                    rating_xpath = f"{base_xpath}/div[1]/div[2]/div/input"
                    page.wait_for_selector(rating_xpath)
                    rating = page.locator(rating_xpath).get_attribute("value")

                    wrt_date_xpath = f"{base_xpath}/div[1]/div[1]/div/span[4]"
                    page.wait_for_selector(wrt_date_xpath)
                    wrt_date = page.locator(
                        wrt_date_xpath).inner_text().replace(".", "-")

                    review.update({'isbn': isbn, 'web_code': WEBCODE,
                                  'content': content, 'rating': rating, 'wrt_date': wrt_date})
                    # 결과 출력
                    print(f"review {review_cnt}번째")
                    print(f"rating: {rating}")
                    # print(f"content: {content}")
                    print(f"date: {wrt_date}")
                    print('-' * 50)

                    reviews.append(review)
                    review_cnt += 1

                try:
                    next_button = page.locator(next_button_xpath)
                    if not next_button.is_visible() or next_button.is_disabled():
                        break
                    next_button.click()
                    page_num += 1
                except Exception as e:
                    print(f"Error encountered: {e}")
                    break

            print(f"{isbn} 리뷰 수집 끝!")
        browser.close()
    return reviews, contents


def upload_to_s3(bucket_name, data, type, DATE, WEBCODE, BOOK_TYPE):
    df = pd.DataFrame(data)
    fs = s3fs.S3FileSystem(anon=False)
    if type == "review":
        bucket_path = f"s3://{bucket_name}/curated/review/{DATE}/{BOOK_TYPE}_book_reviews_{WEBCODE}.parquet"
    else:
        bucket_path = f"s3://{bucket_name}/curated/book_content/{DATE}/{BOOK_TYPE}_book_contents.parquet"
    print(f"{bucket_path} 파일 업로드 시작")
    with fs.open(bucket_path, 'wb') as f:
        df.to_parquet(f)
    print(f"{bucket_path} 파일 업로드 완료!")


def main():
    load_dotenv()
    if len(sys.argv) < 2:
        sys.exit(1)

    print(sys.argv)
    DATE = sys.argv[1]
    WEBCODE = sys.argv[2]
    BOOK_TYPE = sys.argv[3]
    print(WEBCODE, DATE)

    bucket_name = os.environ.get("BUCKET_NAME")
    isbn_object_key = f"raw/isbn/{DATE}/{BOOK_TYPE}.csv"
    isbn_list = get_isbn_list(bucket_name, isbn_object_key)
    if len(isbn_list) == 0:
        print(f"{isbn_object_key}의 파일이 존재하지 않습니다.")
        return
    reviews, contents = scrap_review_and_content(isbn_list, WEBCODE)
    upload_to_s3(bucket_name, reviews, "review")
    upload_to_s3(bucket_name, contents, "content")


if __name__ == "__main__":
    main()
