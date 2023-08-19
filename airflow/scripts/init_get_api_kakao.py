
import os
from dotenv import load_dotenv
import requests
from utils.file_operations import upload_files_to_s3
from utils.api_operations import get_isbn_list
from utils.api_operations import get_headers
from utils.api_operations import save_json_file
from utils.file_operations import get_file_cnt

load_dotenv()
BUCKET_NAME = os.environ.get("BUCKET_NAME")
site = 'kakao'
source_dir = 'data/'
TODAY = '2023-08-16'


def fetch_kakao_api_data(isbn_list):
    books = {'items': []}
    valid_isbn_cnt = 0
    key_num = 1
    url = "https://dapi.kakao.com/v3/search/book"
    headers = get_headers(site, key_num)

    for i, isbn in enumerate(isbn_list):
        params = {
            "query": isbn,
            "target": "isbn"
        }

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
                headers = get_headers(site, key_num)
                print(f'API Key {key_num}으로 변경')
                continue

        book_info = response.json()
        if book_info.get("total") == 0:
            print(f'{site} {i} 번째 {isbn} book info 없음!')
            continue

        books['items'].append(book_info)
        valid_isbn_cnt += 1
        print(f'{site} {i} 번째 {isbn} book info 수집, 현재 {valid_isbn_cnt}개')

    return books


def main():
    object_path = f'raw/isbn/{TODAY}/init/'
    file_cnt = get_file_cnt(BUCKET_NAME, object_path)

    for i in range(1, file_cnt + 1):
        isbn_object_key = f'raw/isbn/{TODAY}/init/{i}.csv'
        isbn_list = get_isbn_list(BUCKET_NAME, isbn_object_key)
        books = fetch_kakao_api_data(isbn_list)
        json_file_path = f"{source_dir}{site}/raw+book_info+{site}+{TODAY}+init+books_{i}.json"
        save_json_file(json_file_path, books)
        upload_files_to_s3(BUCKET_NAME, f'{source_dir}{site}/')


if __name__ == "__main__":
    main()
