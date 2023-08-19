from utils.connections import get_s3_client
import pandas as pd
import os
from io import StringIO
import requests
import time
import json
import csv


def get_isbn_list(bucket_name, object_key):
    s3_client = get_s3_client()
    response = s3_client.get_object(Bucket=bucket_name, Key=object_key)
    csv_content = response['Body'].read().decode('utf-8')
    df = pd.read_csv(StringIO(csv_content))
    return df['ISBN'].tolist()


def get_naver_api_key(number) -> dict:
    keys = {
        i: {
            "naver_client_id": os.environ.get(f"NAVER_CLIENT_ID_{i}"),
            "naver_client_secret": os.environ.get(f"NAVER_CLIENT_SECRET_{i}")
        }
        for i in range(1, 21)
    }
    return keys[number]


def get_kakao_api_key(number) -> dict:
    keys = {
        i: {
            "kakao_api_key": os.environ.get(f"KAKAO_REST_API_KEY_{i}")
        }
        for i in range(1, 4)
    }
    return keys[number]


def get_headers(site, key_num) -> dict:
    headers = {}
    if site == 'naver':
        key = get_naver_api_key(key_num)
        naver_client_id = key.get('naver_client_id')
        naver_client_secret = key.get('naver_client_secret')
        headers = {
            "X-Naver-Client-Id": naver_client_id,
            "X-Naver-Client-Secret": naver_client_secret
        }
    elif site == 'kakao':
        key = get_kakao_api_key(key_num)
        kakao_api_key = key.get('kakao_api_key')
        headers = {
            "Authorization": f'KakaoAK {kakao_api_key}'
        }
    return headers


def save_csv_file(file_path, isbn_list):
    directory = os.path.dirname(file_path)

    if not os.path.exists(directory):
        os.makedirs(directory)

    with open(file_path, 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(["ISBN"])
        for isbn in isbn_list:
            writer.writerow([isbn])


def save_json_file(file_path, items):
    directory = os.path.dirname(file_path)

    if not os.path.exists(directory):
        os.makedirs(directory)

    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(items, f, ensure_ascii=False, indent=4)


def fetch_api_data(isbn_list, site):
    url = ''
    headers = {}
    params = {}
    books = {'items': []}

    # site별 header 설정
    if site == 'naver':
        url = "https://openapi.naver.com/v1/search/book.json"
        naver_client_id = os.environ.get("NAVER_CLIENT_ID")
        naver_client_secret = os.environ.get("NAVER_CLIENT_SECRET")
        headers = {
            "X-Naver-Client-Id": naver_client_id,
            "X-Naver-Client-Secret": naver_client_secret
        }
    elif site == 'kakao':
        url = "https://dapi.kakao.com/v3/search/book"
        kakao_rest_api_key = os.environ.get("KAKAO_REST_API_KEY")
        headers = {
            "Authorization": f'KakaoAK {kakao_rest_api_key}'
        }

    for i, isbn in enumerate(isbn_list):
        if site == 'naver' and len(isbn) == 10:
            continue

        if site == 'naver':
            params = {
                "query": isbn,
                "start": '1'
            }
        elif site == 'kakao':
            params = {
                "query": isbn,
                "target": "isbn"
            }
        elif site == 'aladin':
            url = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx"
            aladin_rest_api_key = os.environ.get("TTB_KEY")
            params = {
                "ttbkey": aladin_rest_api_key,
                "itemIdType": "ISBN13",
                "ItemId": isbn,
                "output": "JS",
                "Version": 20131101,
                "OptResult": "bestSellerRank"
            }

        if site == 'naver':
            time.sleep(0.2)

        try:
            response = requests.get(url, headers=headers, params=params)
            response.raise_for_status()
        except requests.exceptions.RequestException as e:
            print(f"Error while fetching data: {e}")

        book_info = response.json()
        if site == 'naver' and book_info['total'] == 0:
            print(f'{site} {i}번째 book info 없음')
            continue
        elif site == 'kakao' and book_info['meta']['total_count'] == 0:
            print(f'{site} {i}번째 book info 없음')
            continue
        elif site == 'aladin' and book_info['errorCode'] == 8:
            print(f'{site} {i}번째 book info 없음')
            continue
            
        books['items'].append(book_info)
        print(f'{site} {i}번째 book info 수집')

    return books
