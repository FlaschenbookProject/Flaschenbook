from utils.connetions import get_s3_client
import pandas as pd
import os
from io import StringIO
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
import requests
import time


def get_isbn_list(bucket_name, object_key):
    s3_client = get_s3_client()
    response = s3_client.get_object(Bucket=bucket_name, Key=object_key)
    csv_content = response['Body'].read().decode('utf-8')
    df = pd.read_csv(StringIO(csv_content))
    return df['ISBN'].tolist()


def fetch_api_data(isbn_list, site):
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
