
import json
import os
from datetime import date
from dotenv import load_dotenv
import requests
import time
from utils.file_operations import upload_files_to_s3
from utils.api_operations import get_isbn_list
import csv

load_dotenv()
BUCKET_NAME = os.environ.get("BUCKET_NAME")
site = 'naver'
source_dir = 'data/'
TODAY = date.today().strftime("%Y-%m-%d")
TODAY = '2023-08-16'


def get_naver_api_key(number) -> dict:
    keys = {
        i: {
            "naver_client_id": os.environ.get(f"NAVER_CLIENT_ID_{i}"),
            "naver_client_secret": os.environ.get(f"NAVER_CLIENT_SECRET_{i}")
        }
        for i in range(1, 21)
    }

    return keys[number]


def get_new_headers(key_num) -> dict:
    keys = get_naver_api_key(key_num)
    naver_client_id = keys.get('naver_client_id')
    naver_client_secret = keys.get('naver_client_secret')
    headers = {
        "X-Naver-Client-Id": naver_client_id,
        "X-Naver-Client-Secret": naver_client_secret
    }
    return headers


def save_files(isbn_list, books, file_num):
    file_path = f'{source_dir}isbn/raw+isbn+{TODAY}+init+{file_num}.csv'

    # 디렉터리가 존재하는지 확인하고, 없으면 생성
    directory = os.path.dirname(file_path)
    if not os.path.exists(directory):
        os.makedirs(directory)

    with open(file_path, 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(["ISBN"])
        for isbn in isbn_list:
            writer.writerow([isbn])

    file_path = f"{source_dir}{site}/raw+book_info+{site}+{TODAY}+init+books_{file_num}.json"
    directory = os.path.dirname(file_path)
    if not os.path.exists(directory):
        os.makedirs(directory)
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(books, f, ensure_ascii=False, indent=4)


def fetch_api_data(isbn_list):
    books = {'items': []}
    valid_isbn_list = []
    valid_isbn_cnt = 0
    key_num = 1
    file_num = 1
    url = "https://openapi.naver.com/v1/search/book.json"
    headers = get_new_headers(key_num)

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
                headers = get_new_headers(key_num)
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
            save_files(valid_isbn_list, books, file_num)
            file_num += 1
            valid_isbn_list = []
            books = {'items': []}

    save_files(valid_isbn_list, books, file_num)


def main():
    isbn_object_key = f'raw/isbn/{TODAY}/raw.csv'
    isbn_list = get_isbn_list(BUCKET_NAME, isbn_object_key)
    fetch_api_data(isbn_list)
    upload_files_to_s3(BUCKET_NAME, f'{source_dir}isbn/')
    upload_files_to_s3(BUCKET_NAME, f'{source_dir}{site}/')


if __name__ == "__main__":
    main()
