import requests
import os
from datetime import datetime
import pandas as pd
from dotenv import load_dotenv
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

# JSON 데이터를 가져오는 함수
def get_json_data(page_no, lib_key):
    url = "https://www.nl.go.kr/seoji/SearchApi.do?cert_key=" + lib_key
    option = "&result_style=json&page_size=500&ebook_yn=N&page_no=" + str(page_no)
    
    retry = Retry(total=3, backoff_factor=1)  # 최대 3번까지 재시도, 간격은 1초씩 증가
    adapter = HTTPAdapter(max_retries=retry) # 재시도를 하기 위한 http 연결 관리
    http = requests.Session()
    http.mount("https://", adapter)
    
    try:
        response = http.get(url + option) 
        response.raise_for_status() #요청이 실패했을 시 connection error 발생
        return response.json()
    #connection 오류 발생 시 예외 처리
    except requests.exceptions.RequestException as e:
        print(f"Error while fetching data: {e}")
        return None

def extract_isbn(data):
    if data is None:
        return []

    isbn = []
    docs = data.get("docs", [])

    for doc in docs:
        if doc.get("EA_ISBN"):
            isbn.append(doc.get("EA_ISBN"))
            
    return isbn

#csv를 저장
def save_to_csv(isbn_list, filename):
    df = pd.DataFrame({"ISBN": isbn_list})  # 데이터프레임 생성
    df.to_csv(filename, index=False) 

def main(api_key):
    isbn_list = []

    #limit 호출 횟수를 감안하여 첫 번째 페이지는 따로 탐색해 전체 페이지 확인 후 isbn 작업 처리
    json_data = get_json_data(1, api_key)
    total_page = int(json_data["TOTAL_COUNT"])//500
    isbn_list.extend(extract_isbn(json_data))

    #isbn_list 생성 (나머지 페이지에 대해서)
    #1119까지 완료
    for i in range(2, total_page//5+1):
        data = get_json_data(i, api_key)
        isbn_list.extend(extract_isbn(data)) 
        print(f"success page number: {i}")
    
    #csv 파일 저장
    output_path = "airflow/data"
    today = datetime.now().strftime("%Y-%m-%d")
    os.makedirs(output_path, exist_ok = True)
    csv_filename = os.path.join(output_path, f"{today}_init.csv")
    
    save_to_csv(isbn_list, csv_filename)
    
    print(f"complete to save {today}_init.csv")

if __name__ == "__main__":
    load_dotenv() #env 파일 로드
    
    api_key = os.getenv("LIB_KEY")
    isbn_list = main(api_key)