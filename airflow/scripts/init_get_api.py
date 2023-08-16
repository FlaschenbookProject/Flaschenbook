import requests
import json
import os
import glob
import boto3
from datetime import date
from dotenv import load_dotenv
import pandas as pd
from io import StringIO

BUCKET_NAME = os.environ.get("BUCKET_NAME")
TODAY = date.today().strftime("%Y-%m-%d")


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
            print(f"Uploaded {file_path} to {bucket_name}/{object_key}")
            os.remove(file_path)


def main():
    load_dotenv()
    site_list = ['naver', 'kakao']
    isbn_list = get_isbn_list()

    for site in site_list:
        url = ''
        headers = {}
        params = {}
        books = {'items': []}

        for isbn in isbn_list:
            if site == 'naver' and len(isbn) == 10:
                continue

            if site == 'naver':
                url = f"https://openapi.naver.com/v1/search/book.json?query={isbn}&start=1"
                naver_client_id = os.environ.get("NAVER_CLIENT_ID")
                naver_client_secret = os.environ.get("NAVER_CLIENT_SECRET")
                headers = {
                    "X-Naver-Client-Id": naver_client_id,
                    "X-Naver-Client-Secret": naver_client_secret
                }
            elif site == 'kakao':
                url = "https://dapi.kakao.com/v3/search/book?target=isbn"
                kakao_rest_api_key = os.environ.get("KAKAO_REST_API_KEY")
                headers = {
                    "Authorization": f'KakaoAK {kakao_rest_api_key}'
                }
                params = {
                    "query": isbn
                }

            response = requests.get(url, headers=headers, params=params)
            response.raise_for_status()
            book_info = response.json()

            if site == 'naver' and book_info['total'] == 0:
                continue
            elif site == 'kakao' and book_info['meta']['total_count'] == 0:
                continue

            books['items'].append(book_info)

        file_path = f"airflow/data/{site}/raw+book_info+{site}+{TODAY}+books.json"
        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(books, f, ensure_ascii=False, indent=4)

        bucket_name = BUCKET_NAME
        source_dir = f'airflow/data/{site}/'
        print(source_dir)
        upload_files_to_s3(bucket_name, source_dir)


if __name__ == "__main__":
    main()
