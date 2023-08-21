import requests
import os
import json
from datetime import date
from dotenv import load_dotenv
from utils.file_operations import upload_files_to_s3
from utils.api_operations import get_isbn_list
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
from utils.file_operations import get_file_cnt
from utils.api_operations import save_json_file


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
        retry = Retry(total=3, connect=0.01, backoff_factor=1)

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
                
        try:       
            # book_info = response.json()
            book_info = json.loads(response.text, strict=False)
            
        except json.decoder.JSONDecodeError as e:
            print(f"JSONDecodeError occurred for ISBN: {isbn}")
            print(f"Error message: {e}")
                
        if book_info.get('errorCode') == 8:
            print(f'{i}번째 book info 없음 isbn: {isbn}')
            continue
        if book_info.get('errorCode') == 10:
            cnt += 1
            print(f'{api_keys[cnt]} key 종료')
            continue
        print(f'{i}번째 book info 수집')
        books['items'].append(book_info)

    return books


def save_json():
    BUCKET_NAME = os.environ.get("BUCKET_NAME")
    SITE = "aladin"
    TODAY = date.today().strftime("%Y-%m-%d")
    # TODAY = "2023-08-16"
    
    isbn_keys = [os.environ.get("TTB_KEY"), os.environ.get("TTB_KEY2"), os.environ.get("TTB_KEY3"), os.environ.get("TTB_KEY4"), os.environ.get("TTB_KEY5"), os.environ.get("TTB_KEY6"), os.environ.get("TTB_KEY7"), os.environ.get("TTB_KEY8"), os.environ.get("TTB_KEY9"), os.environ.get("TTB_KEY10")]

    csv_file_dir = f'raw/isbn/{TODAY}/init/'
    # csv 파일 개수를 조회 
    # for문 돌면서 해당 file_num의 파일에 접근해 isbn 처리
    total_file_num = get_file_cnt(BUCKET_NAME, csv_file_dir)
    
    for i in range(1, total_file_num + 1):
        source_dir = f'airflow/scripts/data/{SITE}/'
        isbn_object_key = f'raw/isbn/{TODAY}/init/{i}.csv'
        isbn_list = get_isbn_list(BUCKET_NAME, isbn_object_key)
        books = fetch_api_data(isbn_list, isbn_keys)
        
        json_file_path = f"{source_dir}{SITE}/raw+book_info+{SITE}+{TODAY}+init+books_{i}.json"
        save_json_file(json_file_path, books)
        upload_files_to_s3(BUCKET_NAME, f'{source_dir}{SITE}/')


def main():
    load_dotenv()  # env 파일 로드
    save_json()


if __name__ == "__main__":
    main()
