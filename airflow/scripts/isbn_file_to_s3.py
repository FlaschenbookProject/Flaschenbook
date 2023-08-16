import os
from datetime import datetime
from dotenv import load_dotenv
import boto3


def main(search_date):
    # S3에 업로드할 파일명
    s3_filename = f"{search_date}.csv"

    # 저장할 로컬 CSV 파일 경로
    local_csv_filename = f"airflow/data/{search_date}.csv"

    # S3 클라이언트 생성
    s3_client = boto3.client(
        's3',
        aws_access_key_id=aws_access_key_id,
        aws_secret_access_key=aws_secret_access_key
    )

    # 폴더가 없으면 생성
    folder_prefix = os.path.dirname(s3_folder_path)
    if folder_prefix and not folder_prefix.endswith('/'):
        folder_prefix += '/'
    s3_client.put_object(Bucket=bucket_name, Key=folder_prefix)

    # 로컬 파일을 S3 버킷에 업로드
    s3_client.upload_file(local_csv_filename, bucket_name,
                          s3_folder_path + os.path.basename(s3_filename))

    print(f"{s3_folder_path}{os.path.basename(s3_filename)} 파일이 S3 버킷에 업로드되었습니다.")


if __name__ == "__main__":
    load_dotenv()  # env 파일 로드
    # 추후 airflow 환경의 날짜를 가지고 와 해당 날짜를 조회하도록 수정 예정
    today = datetime.now().strftime("%Y-%m-%d")

    # AWS 계정 및 리전 설정
    aws_access_key_id = os.getenv("AWS_ACCESS_KEY_ID")
    aws_secret_access_key = os.getenv("AWS_SECRET_ACCESS_KEY")
    bucket_name = os.getenv("BUCKET_NAME")
    s3_folder_path = 'raw/isbn/'
    # 현재 날짜를 yyyy-mm-dd 형식으로 포맷팅

    main(today)
