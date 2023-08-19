import requests
import json
import os
from datetime import date
from dotenv import load_dotenv
from utils.file_operations import upload_files_to_s3
from utils.api_operations import get_isbn_list
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
from utils.file_operations import get_file_cnt


def fetch_api_data(isbn_list, api_keys):
    url = ''
    headers = {}
    params = {}
    books = {'items': []}
    cnt = 0
    
    for i, isbn in enumerate(isbn_list):
        url = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx"
        aladin_rest_api_key = api_keys[cnt]
        params = {
            "ttbkey": aladin_rest_api_key,
            "itemIdType": "ISBN13",
            "ItemId": isbn,
            "output": "JS",
            "Version": 20131101,
            "OptResult": "bestSellerRank"
        }

        # 최대 3번까지 재시도, 간격은 1초씩 증가
        retry = Retry(total=3, backoff_factor=1)

        # 재시도를 하기 위한 http 연결 관리
        adapter = HTTPAdapter(max_retries=retry)

        http = requests.Session()
        http.mount("https://", adapter)

        try:
            response = requests.get(url, headers=headers, params=params)
            response.raise_for_status()

        # connection 오류 발생 시 예외 처리
        except requests.exceptions.RequestException as e:
            if response.status_code == 429:
                print("API 호출 횟수가 제한되었습니다.")
                cnt += 1
                continue
            else:
                print(f"Error while fetching data: {e}")

        book_info = response.json()
        if book_info.get('errorCode') == 8:
            print(f'{i}번째 book info 없음 isbn: {isbn}')
            continue
        print(f'{i}번째 book info 수집')
        books['items'].append(book_info)

    return books


def main():
    load_dotenv()  # env 파일 로드
    BUCKET_NAME = os.environ.get("BUCKET_NAME")
    #TODAY = date.today().strftime("%Y-%m-%d")
    TODAY = "2023-08-16"
    
    source_directory = f'raw/isbn/{TODAY}/init/'
    site = "aladin"
    isbn_keys = [os.environ.get("TTB_KEY"), os.environ.get("TTB_KEY2"), os.environ.get("TTB_KEY3"), os.environ.get("TTB_KEY4"), os.environ.get("TTB_KEY5")]
    
    file_cnt = get_file_cnt(BUCKET_NAME, source_directory)
    
    source_dir = f'airflow/scripts/data/{site}/'
    file_path = f"{source_dir}raw+book_info+{site}+{TODAY}+init+books.json"
    isbn_list = get_isbn_list(BUCKET_NAME, source_directory)
      
    books = fetch_api_data(isbn_list, isbn_keys)
    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(books, f, ensure_ascii=False, indent=4)
    upload_files_to_s3(BUCKET_NAME, source_dir)


if __name__ == "__main__":
    main()
