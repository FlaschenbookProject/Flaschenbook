import requests
import os
from datetime import datetime
import pandas as pd
from dotenv import load_dotenv
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry


# JSON 데이터를 가져오는 함수
def get_json_data(page_no, api_key):
    url = "http://www.aladin.co.kr/ttb/api/ItemList.aspx?ttbkey=" + api_key
    option = "&QueryType=ItemNewAll&MaxResults=100&SearchTarget=Book&output=JS&Version=20131101&start=" + \
        str(page_no)

    retry = Retry(total=3, backoff_factor=1)  # 최대 3번까지 재시도, 간격은 1초씩 증가
    adapter = HTTPAdapter(max_retries=retry)  # 재시도를 하기 위한 http 연결 관리
    http = requests.Session()
    http.mount("https://", adapter)

    try:
        response = http.get(url + option)
        response.raise_for_status()  # 요청이 실패했을 시 connection error 발생
        return response.json()
    # connection 오류 발생 시 예외 처리
    except requests.exceptions.RequestException as e:
        print(f"Error while fetching data: {e}")
        return None


def extract_isbn(data):
    if data is None:
        return []

    isbn = []
    items = data.get("item", [])

    for item in items:
        if item.get("pubDate") == today:
            isbn.append(item.get("isbn13"))

    return isbn


# csv를 저장
def save_to_csv(isbn_list, filename):
    df = pd.DataFrame({"ISBN": isbn_list})  # 데이터프레임 생성
    df.to_csv(filename, index=False)


def main(api_key):
    new_isbn_list = []  # 오늘 날짜의 신간 isbn을 저장할 List

    # limit 호출 횟수를 감안하여 첫 번째 페이지는 따로 탐색해 전체 페이지 확인 후 isbn 작업 처리
    json_data = get_json_data(1, api_key)
    total_page = int(json_data["totalResults"]) // int(json_data["itemsPerPage"])
    new_isbn_list.extend(extract_isbn(json_data))

    for i in range(2, total_page + 1):
        data = get_json_data(i, api_key)
        isbn_list.extend(extract_isbn(data))
        print(f"success page number: {i}")

    # csv 파일 저장
    output_path = "airflow/data"
    today = datetime.now().strftime("%Y-%m-%d")
    os.makedirs(output_path, exist_ok=True)
    csv_filename = os.path.join(output_path, f"{today}.csv")

    save_to_csv(isbn_list, csv_filename)

    print(f"complete to save {today}.csv")


if __name__ == "__main__":
    load_dotenv()  # env 파일 로드
    # 추후 airflow 환경의 날짜를 가지고 와 해당 날짜를 조회하도록 수정 예정
    today = datetime.now().strftime("%Y-%m-%d")

    api_key = os.getenv("TTB_KEY")
    isbn_list = main(api_key)
