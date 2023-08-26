import requests
import os
import sys
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry
from utils.file_operations import save_to_csv, upload_files_to_s3


# JSON 데이터를 가져오는 함수
def get_json_data(page_no, api_key):
    url = "http://www.aladin.co.kr/ttb/api/ItemList.aspx"
    params = {
        "ttbkey": api_key,
        "QueryType": "ItemNewAll",
        "MaxResults": 100,
        "SearchTarget": "Book",
        "output": "JS",
        "Version": 20131101,
        "start": page_no
    }
    retry = Retry(total=3, backoff_factor=1)  # 최대 3번까지 재시도, 간격은 1초씩 증가
    adapter = HTTPAdapter(max_retries=retry)  # 재시도를 하기 위한 http 연결 관리
    http = requests.Session()
    http.mount("https://", adapter)

    try:
        response = http.get(url, params=params)
        response.raise_for_status()  # 요청이 실패했을 시 connection error 발생
        return response.json()
    # connection 오류 발생 시 예외 처리
    except requests.exceptions.RequestException as e:
        print(f"Error while fetching data: {e}")
        return None


def extract_isbn(data, today):
    if data is None:
        print("데이터 없음")
        return []

    isbn = []
    items = data.get("item", [])

    for item in items:
        if item.get("pubDate") == today:
            isbn.append(item.get("isbn13"))
            print(item.get("isbn13"))

    return isbn


def main():
    api_key = os.getenv("TTB_KEY")
    bucket_name = os.getenv("BUCKET_NAME")

    if len(sys.argv) < 2:
        sys.exit(1)

    print(sys.argv)
    today = sys.argv[1]
    print(f"{today} New Book")

    new_isbn_list = []  # 오늘 날짜의 신간 isbn을 저장할 List

    # limit 호출 횟수를 감안하여 첫 번째 페이지는 따로 탐색해 전체 페이지 확인 후 isbn 작업 처리
    json_data = get_json_data(1, api_key)
    total_page = int(json_data["totalResults"]
                     ) // int(json_data["itemsPerPage"])
    new_isbn_list.extend(extract_isbn(json_data, today))

    for i in range(2, total_page + 1):
        data = get_json_data(i, api_key)
        new_isbn_list.extend(extract_isbn(data, today))
        print(f"success page number: {i}")

    # csv 파일 저장
    output_path = "data/isbn/"
    os.makedirs(output_path, exist_ok=True)
    csv_filename = os.path.join(output_path, f"raw+isbn+{today}+new.csv")

    save_to_csv(new_isbn_list, csv_filename)
    upload_files_to_s3(bucket_name, output_path)


if __name__ == "__main__":
    main()
