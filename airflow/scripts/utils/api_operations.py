from typing import List, Dict
from utils.connections import get_s3_client
import pandas as pd
import os
from io import StringIO
import requests
import time
import csv


def get_isbn_list(bucket_name: str, object_key: str) -> List[str]:
    """
    S3에서 ISBN 목록을 CSV로부터 가져오는 함수.

    Args:
        bucket_name (str): S3 버킷 이름
        object_key (str): 가져올 파일의 S3 경로

    Returns:
        list: ISBN 목록
    """
    s3_client = get_s3_client()
    response = s3_client.get_object(Bucket=bucket_name, Key=object_key)
    csv_content = response['Body'].read().decode('utf-8')
    df = pd.read_csv(StringIO(csv_content))
    df = df.dropna(subset=['ISBN'])
    df['ISBN'] = df['ISBN'].astype(str).str.split('.').str[0]

    return df['ISBN'].tolist()


def get_naver_api_key(number: int) -> Dict[str, str]:
    """
    주어진 번호에 해당하는 NAVER API 키를 반환합니다.

    Args:
        number (int): 사용할 API 키 번호.

    Returns:
        dict: NAVER client ID와 client secret 정보
    """
    keys = {
        i: {
            "naver_client_id": os.environ.get(f"NAVER_CLIENT_ID_{i}"),
            "naver_client_secret": os.environ.get(f"NAVER_CLIENT_SECRET_{i}")
        }
        for i in range(1, 21)
    }
    return keys[number]


def get_kakao_api_key(number: int) -> Dict[str, str]:
    """
    주어진 번호에 해당하는 KAKAO API 키를 반환합니다.

    Args:
        number (int): 사용할 API 키 번호.

    Returns:
        dict: KAKAO REST API key 정보
    """
    keys = {
        i: {
            "kakao_api_key": os.environ.get(f"KAKAO_REST_API_KEY_{i}")
        }
        for i in range(1, 4)
    }
    return keys[number]


def get_headers(site: str, key_num: int) -> Dict[str, str]:
    """
    사이트 이름과 키 번호를 기반으로 API 호출 headers를 반환합니다.

    Args:
        site (str): API를 호출할 사이트 이름 ('naver' 또는 'kakao')
        key_num (int): 사용할 API 키 번호

    Returns:
        dict: API 호출에 필요한 headers
    """
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


def save_csv_file(file_path: str, isbn_list: List[str]) -> None:
    """
    ISBN 목록을 주어진 경로의 CSV 파일로 저장합니다. (init이 아닌 데이터 로직에서 사용하는 save_csv_file은 목적에 따라 file_operations로 이동)

    Args:
        file_path (str): ISBN 목록을 저장할 파일 경로
        isbn_list (list): 저장할 ISBN 목록
    """
    directory = os.path.dirname(file_path)

    if not os.path.exists(directory):
        os.makedirs(directory)

    with open(file_path, 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(["ISBN"])
        for isbn in isbn_list:
            writer.writerow([isbn])


def fetch_api_data(isbn_list: List[str], site: str) -> Dict[str, Dict]:
    """
    주어진 ISBN 목록과 사이트 정보를 사용하여 API에서 데이터를 가져옵니다.

    Args:
        isbn_list (list): 가져올 책의 ISBN 목록
        site (str): 데이터를 가져올 사이트 이름

    Returns:
        dict: 수집된 책 정보
    """
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
            time.sleep(0.1)

        try:
            response = requests.get(url, headers=headers, params=params)
            response.raise_for_status()
        except requests.exceptions.RequestException as e:
            print(f"Error while fetching data: {e}")

        book_info = response.json()
        if site == 'naver' and book_info['total'] == 0:
            print(f'{site} {i} 번째 {isbn} book info 없음!')
            continue
        elif site == 'kakao' and book_info['meta']['total_count'] == 0:
            print(f'{site} {i} 번째 {isbn} book info 없음!')
            continue
        elif site == 'aladin' and book_info['errorCode'] == 8:
            print(f'{site} {i} 번째 {isbn} book info 없음!')
            continue

        books['items'].append(book_info)
        print(f'{site} {i} 번째 {isbn} book info 수집')

    return books
