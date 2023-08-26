import os
from utils.file_operations import upload_files_to_s3


def check_files_in_directory(directory):
    try:
        # 디렉토리 내의 파일 목록 얻기
        files = os.listdir(directory)

        if files:
            print("디렉토리에 파일이 있습니다.")
        else:
            print("디렉토리에 파일이 없습니다.")
    except Exception as e:
        print(f"Error: {e}")


def main():
    bucket_name = os.getenv("BUCKET_NAME")
    source_dir = "data/isbn/"
    check_files_in_directory(source_dir)
    upload_files_to_s3(bucket_name, source_dir)


if __name__ == "__main__":
    main()
