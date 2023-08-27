from typing import List, Dict
import os
from dotenv import load_dotenv
import requests
from utils.file_operations import upload_files_to_s3
from utils.api_operations import get_isbn_list
from utils.api_operations import get_headers
from utils.file_operations import save_json_file
from utils.file_operations import get_file_cnt

SITE = 'kakao'


def fetch_kakao_api_data(isbn_list: List[str]) -> Dict[str, List[Dict]]:
    """
    ISBN 리스트를 사용하여 Kakao 책 검색 API에서 책 정보를 가져옵니다.

    Args:
        isbn_list (List[str]): 검색할 ISBN 번호의 리스트

    Returns:
        Dict[str, List[Dict]]: 수집된 책 정보
    """
    books = {'items': []}
    valid_isbn_cnt = 0
    key_num = 1
    url = "https://dapi.kakao.com/v3/search/book"
    headers = get_headers(SITE, key_num)

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
                headers = get_headers(SITE, key_num)
                print(f'API Key {key_num}으로 변경')
                continue

        book_info = response.json()
        if book_info.get("total") == 0:
            print(f'{SITE} {i} 번째 {isbn} book info 없음!')
            continue

        books['items'].append(book_info)
        valid_isbn_cnt += 1
        print(f'{SITE} {i} 번째 {isbn} book info 수집, 현재 {valid_isbn_cnt}개')

    return books


def main():
    """
    1. 환경 변수 로드
    2. S3 버킷에서 ISBN 목록을 가져옴
    3. 각 ISBN에 대해 Kakao 책 검색 API를 사용하여 책 정보를 가져옴
    4. 가져온 책 정보를 JSON 파일로 저장
    5. 해당 JSON 파일을 S3 버킷에 업로드
    """
    load_dotenv()  # 환경 변수 로드

    # S3 버킷 및 다른 환경 변수 설정
    bucket_name = os.environ.get("BUCKET_NAME")
    source_dir = 'data/'
    date = '2023-08-16'
    object_path = f'raw/isbn/{date}/init/'

    # 지정된 S3 경로에 있는 파일의 수를 얻음
    file_cnt = get_file_cnt(bucket_name, object_path)

    # 각 파일에 대한 처리 시작
    for i in range(1, file_cnt + 1):
        # ISBN 목록을 S3에서 가져옴
        isbn_object_key = f'raw/isbn/{date}/init/{i}.csv'
        isbn_list = get_isbn_list(bucket_name, isbn_object_key)

        # ISBN 목록을 사용하여 책 정보를 가져옴
        books = fetch_kakao_api_data(isbn_list)

        # 가져온 책 정보를 JSON 파일로 저장
        json_file_path = f"{source_dir}{SITE}/raw+book_info+{SITE}+{date}+init+books_{i}.json"
        save_json_file(json_file_path, books)

        # 저장된 JSON 파일을 S3 버킷에 업로드
        upload_files_to_s3(bucket_name, f'{source_dir}{SITE}/')


if __name__ == "__main__":
    main()
