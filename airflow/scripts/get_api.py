import os
from datetime import date
from dotenv import load_dotenv
from utils.file_operations import upload_files_to_s3
from utils.api_operations import get_isbn_list
from utils.api_operations import fetch_api_data
from utils.api_operations import save_json_file

load_dotenv()
BUCKET_NAME = os.environ.get("BUCKET_NAME")
BOOK_SITE = os.environ.get("BOOK_SITE")
TODAY = date.today().strftime("%Y-%m-%d")


def main():
    isbn_object_key = f'raw/isbn/{TODAY}/new.csv'
    source_dir = f'data/{BOOK_SITE}/'
    file_path = f"{source_dir}raw+book_info+{BOOK_SITE}+{TODAY}+new.json"
    isbn_list = get_isbn_list(BUCKET_NAME, isbn_object_key)
    books = fetch_api_data(isbn_list, BOOK_SITE)
    save_json_file(file_path, books)
    upload_files_to_s3(BUCKET_NAME, source_dir)


if __name__ == "__main__":
    main()
