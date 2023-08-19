import os
from dotenv import load_dotenv
from utils.file_operations import upload_files_to_s3


def main():
    load_dotenv()  # env 파일 로드
    
    bucket_name = os.getenv("BUCKET_NAME")
    '''
    2023-08-17 이전 코드 - 공통 함수화로 변경
    # S3에 업로드할 파일명
    s3_filename = f"{search_date}.csv"
    
    # S3 클라이언트 생성
    s3_client = connections.get_s3_client()

    # 폴더가 없으면 생성
    folder_prefix = os.path.dirname(s3_folder_path)
    if folder_prefix and not folder_prefix.endswith('/'):
        folder_prefix += '/'
    s3_client.put_object(Bucket=bucket_name, Key=folder_prefix)

    # 로컬 파일을 S3 버킷에 업로드
    s3_client.upload_file(local_csv_filename, bucket_name,
                          s3_folder_path + os.path.basename(s3_filename))
    '''
    source_dir = 'airflow/scripts/data/isbn/'
    upload_files_to_s3(bucket_name, source_dir)
    

if __name__ == "__main__":
    main()
