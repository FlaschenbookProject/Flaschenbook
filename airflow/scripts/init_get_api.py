import json
import os
from datetime import date
from dotenv import load_dotenv
from utils.file_operations import upload_files_to_s3
from utils.api_operations import get_isbn_list
from utils.api_operations import fetch_api_data


BUCKET_NAME = os.environ.get("BUCKET_NAME")
TODAY = date.today().strftime("%Y-%m-%d")
TODAY = '2023-08-16'


def main():
    load_dotenv()
    site_list = ['naver', 'kakao']
    isbn_object_key = f'raw/isbn/{TODAY}_init.csv'

    for site in site_list:
        source_dir = f'airflow/data/{site}/'
        file_path = f"{source_dir}raw+book_info+{site}+{TODAY}+books_init.json"
        isbn_list = get_isbn_list(BUCKET_NAME, isbn_object_key)
        books = fetch_api_data(isbn_list, site)
        with open(file_path, "w", encoding="utf-8") as f:
            json.dump(books, f, ensure_ascii=False, indent=4)
        upload_files_to_s3(BUCKET_NAME, source_dir)


if __name__ == "__main__":
    main()
