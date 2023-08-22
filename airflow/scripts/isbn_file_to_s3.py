import os
from dotenv import load_dotenv
from utils.file_operations import upload_files_to_s3


def main():
    load_dotenv()  # env 파일 로드

    bucket_name = os.getenv("BUCKET_NAME")
    source_dir = 'airflow/scripts/data/isbn/'
    upload_files_to_s3(bucket_name, source_dir)


if __name__ == "__main__":
    main()
