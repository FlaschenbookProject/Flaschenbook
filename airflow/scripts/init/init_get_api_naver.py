
import os
from dotenv import load_dotenv
import requests
import time
from utils.file_operations import upload_files_to_s3
from utils.api_operations import get_isbn_list
from utils.api_operations import save_csv_file
from utils.file_operations import save_json_file
from utils.api_operations import get_headers
from typing import List

SITE = 'naver'
SOURCE_DIR = 'data/'
DATE = '2023-08-16'


def fetch_naver_api_data(isbn_list: List[str]) -> None:
    """
    주어진 ISBN 목록에 대한 책 정보를 Naver API에서 가져오면서 유효한 isbn리스트와 책 정보를 각각 csv, json파일로 5000개씩 나누어 저장합니다.

    Args:
        isbn_list (List[str]): 조회할 ISBN 목록

    Returns:
        None
    """
    books = {'items': []}
    valid_isbn_list = []
    valid_isbn_cnt = 0
    key_num = 1
    file_num = 1
    url = "https://openapi.naver.com/v1/search/book.json"
    headers = get_headers(SITE, key_num)
    csv_file_path = ""
    json_file_path = ""

    for i, isbn in enumerate(isbn_list):
        params = {
            "query": isbn,
            "start": '1'
        }

        time.sleep(0.1)

        try:
            response = requests.get(url, headers=headers, params=params)
            response.raise_for_status()
        except requests.exceptions.RequestException as e:
            print(f"Error while fetching data: {e}")
            if e.response.status_code == 429:
                print('Too Many Requests for url')
                isbn_list.append(isbn)

                # key 변경
                key_num += 1
                headers = get_headers(SITE, key_num)
                print(f'API Key {key_num}으로 변경')

                continue

        book_info = response.json()
        if book_info.get("total") == 0:
            print(f'naver {i} 번째 {isbn} book info 없음!')
            continue

        books['items'].append(book_info)
        valid_isbn_list.append(isbn)
        valid_isbn_cnt += 1

        print(f'naver {i} 번째 {isbn} book info 수집, 현재 {valid_isbn_cnt}개')

        # 5000개 간격으로 나눠서 파일 저장
        if valid_isbn_cnt % 5000 == 0:
            csv_file_path = f"{SOURCE_DIR}isbn/raw+isbn+{DATE}+init+{file_num}.csv"
            json_file_path = f"{SOURCE_DIR}{SITE}/raw+book_info+{SITE}+{DATE}+init+books_{file_num}.json"
            save_csv_file(csv_file_path, valid_isbn_list)
            save_json_file(json_file_path, books)
            file_num += 1
            valid_isbn_list = []
            books = {'items': []}

    # 나머지 저장
    csv_file_path = f"{SOURCE_DIR}isbn/raw+isbn+{DATE}+init+{file_num}.csv"
    json_file_path = f"{SOURCE_DIR}{SITE}/raw+book_info+{SITE}+{DATE}+init+books_{file_num}.json"
    save_csv_file(csv_file_path, valid_isbn_list)
    save_json_file(json_file_path, books)


def main():
    """
    1. 환경 변수에서 필요한 정보를 가져옴
    2. ISBN 목록을 S3에서 가져옴
    3. 가져온 ISBN 목록을 사용하여 Naver API로 책 정보를 가져오면서 유효한 isbn을 걸러내어 csv파일로 5000개씩 나누어 생성, 그와 동시에 책 정보 json파일로 5000개씩 나누어 생성
    4. 가져온 책 정보를 S3에 업로드
    """
    load_dotenv()
    BUCKET_NAME = os.environ.get("BUCKET_NAME")
    isbn_object_key = f'raw/isbn/{DATE}/raw.csv'
    isbn_list = get_isbn_list(BUCKET_NAME, isbn_object_key)
    fetch_naver_api_data(isbn_list)
    upload_files_to_s3(BUCKET_NAME, f'{SOURCE_DIR}isbn/')
    upload_files_to_s3(BUCKET_NAME, f'{SOURCE_DIR}{SITE}/')


if __name__ == "__main__":
    main()
