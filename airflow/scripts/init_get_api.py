import requests
import json
import os
import glob
import boto3
from datetime import date
from dotenv import load_dotenv
import pandas as pd
from io import StringIO
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
import time


BUCKET_NAME = os.environ.get("BUCKET_NAME")
TODAY = date.today().strftime("%Y-%m-%d")
TODAY = '2023-08-16'


def get_s3_client():
    # Boto3 S3 client 생성
    s3_client = boto3.client(
        's3',
        aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY")
    )

    return s3_client


def get_isbn_list():
    # Boto3 S3 client 생성
    s3_client = get_s3_client()

    # S3 경로 생성
    object_key = f'raw/isbn/{TODAY}_init.csv'

    response = s3_client.get_object(Bucket=BUCKET_NAME, Key=object_key)
    csv_content = response['Body'].read().decode('utf-8')

    # S3에서 직접 CSV 파일을 DataFrame으로 읽기
    df = pd.read_csv(StringIO(csv_content))

    print(df)

    # 'isbn' 컬럼의 값을 리스트로 반환
    return df['ISBN'].tolist()


def upload_files_to_s3(bucket_name, source_directory):

    # Boto3 S3 client 생성
    s3_client = get_s3_client()

    # '/'로 끝나는지 체크
    if not source_directory.endswith('/'):
        source_directory += '/'

    # glob를 사용하여 모든 파일 탐색
    for file_path in glob.glob(source_directory + '**/*', recursive=True):
        print(file_path)
        if os.path.isfile(file_path):  # 디렉토리는 건너뛰기
            file_name = os.path.basename(file_path)
            object_key = file_name.replace('+', '/')
            print(object_key)
            # 파일을 S3로 업로드
            s3_client.upload_file(file_path, bucket_name, object_key)
            print(f"Uploaded {file_name} to {bucket_name}/{object_key}")
            os.remove(file_path)


def fetch_api_data(site):
    load_dotenv()
    isbn_list = get_isbn_list()

    url = ''
    headers = {}
    params = {}
    books = {'items': []}

    for i, isbn in enumerate(isbn_list):
        if site == 'naver' and len(isbn) == 10:
            continue

        if site == 'naver':
            url = "https://openapi.naver.com/v1/search/book.json"
            naver_client_id = os.environ.get("NAVER_CLIENT_ID")
            naver_client_secret = os.environ.get("NAVER_CLIENT_SECRET")
            headers = {
                "X-Naver-Client-Id": naver_client_id,
                "X-Naver-Client-Secret": naver_client_secret
            }
            params = {
                "query": isbn,
                "start": '1'
            }
        elif site == 'kakao':
            url = "https://dapi.kakao.com/v3/search/book"
            kakao_rest_api_key = os.environ.get("KAKAO_REST_API_KEY")
            headers = {
                "Authorization": f'KakaoAK {kakao_rest_api_key}'
            }
            params = {
                "query": isbn,
                "target": "isbn"
            }

        # 최대 3번까지 재시도, 간격은 1초씩 증가
        retry = Retry(total=3, backoff_factor=1)

        # 재시도를 하기 위한 http 연결 관리
        adapter = HTTPAdapter(max_retries=retry)

        http = requests.Session()
        http.mount("https://", adapter)

        if site == 'naver':
            time.sleep(0.2)

        try:
            response = requests.get(url, headers=headers, params=params)
            response.raise_for_status()

        # connection 오류 발생 시 예외 처리
        except requests.exceptions.RequestException as e:
            print(f"Error while fetching data: {e}")

        book_info = response.json()
        if site == 'naver' and book_info['total'] == 0:
            print(f'{site} {i}번째 book info 없음')
            continue
        elif site == 'kakao' and book_info['meta']['total_count'] == 0:
            print(f'{site} {i}번째 book info 없음')
            continue
        print(f'{site} {i}번째 book info 수집')
        books['items'].append(book_info)

    return books


def main():
    load_dotenv()
    site_list = ['naver', 'kakao']

    for site in site_list:
        source_dir = f'airflow/data/{site}/'
        file_path = f"{source_dir}raw+book_info+{site}+{TODAY}+books_init.json"
        books = fetch_api_data(site)
        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(books, f, ensure_ascii=False, indent=4)
        upload_files_to_s3(BUCKET_NAME, source_dir)


if __name__ == "__main__":
    main()
