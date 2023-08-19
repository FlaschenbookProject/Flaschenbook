import os
from dotenv import load_dotenv
from utils.file_operations import upload_files_to_s3
from utils.api_operations import get_isbn_list
from utils.api_operations import fetch_api_data
from utils.api_operations import save_json_file


def main():
    """
    1. 환경 변수에서 필요한 정보를 가져옴
    2. S3에서 오늘 날짜의 새로운 ISBN 목록을 가져옴
    3. 가져온 ISBN 목록을 사용하여 지정된 책 판매 사이트의 API로 책 정보를 가져옴
    4. 가져온 책 정보를 로컬 JSON 파일로 저장
    5. 저장된 JSON 파일을 S3에 업로드
    """
    load_dotenv()

    BUCKET_NAME = os.environ.get("BUCKET_NAME")
    BOOK_SITE = os.environ.get("BOOK_SITE")
    TODAY = os.environ.get("TODAY")

    isbn_object_key = f"raw/isbn/{TODAY}/new.csv"
    source_dir = f"data/{BOOK_SITE}/"
    file_path = f"{source_dir}raw+book_info+{BOOK_SITE}+{TODAY}+new.json"
    print(BUCKET_NAME, isbn_object_key)

    isbn_list = get_isbn_list(BUCKET_NAME, isbn_object_key)
    books = fetch_api_data(isbn_list, BOOK_SITE)

    save_json_file(file_path, books)
    upload_files_to_s3(BUCKET_NAME, source_dir)


if __name__ == "__main__":
    main()
