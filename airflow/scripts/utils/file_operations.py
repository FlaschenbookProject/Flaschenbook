from typing import List, Dict
from utils.connections import get_s3_client
import os
import glob
import pandas as pd
import json


def save_to_csv(isbn_list: List[str], filename: str) -> None:
    """
    ISBN 리스트를 받아서 CSV 파일로 저장합니다.

    Args:
        isbn_list (List[str]): ISBN 번호의 리스트
        filename (str): 저장할 CSV 파일명

    Returns:
        None
    """
    df = pd.DataFrame({"ISBN": isbn_list})
    df.to_csv(filename, index=False)


def save_json_file(file_path: str, items: Dict[str, Dict]) -> None:
    """
    주어진 항목을 JSON 파일로 저장합니다.

    Args:
        file_path (str): 항목을 저장할 파일 경로
        items (list or dict): JSON 형식으로 저장할 항목
    """
    directory = os.path.dirname(file_path)

    if not os.path.exists(directory):
        os.makedirs(directory)

    with open(file_path, "w", encoding="utf-8") as f:
        json.dump(items, f, ensure_ascii=False, indent=4)


def upload_files_to_s3(bucket_name: str, source_directory: str) -> None:
    """
    지정된 디렉토리의 파일들을 Amazon S3 버킷에 업로드합니다.

    Args:
        bucket_name (str): 대상 S3 버킷 이름
        source_directory (str): 업로드할 파일들이 있는 소스 디렉토리

    Returns:
        None
    """
    s3_client = get_s3_client()

    if not source_directory.endswith('/'):
        source_directory += '/'

    for file_path in glob.glob(source_directory + '**/*', recursive=True):
        print(file_path)
        if os.path.isfile(file_path):
            file_name = os.path.basename(file_path)
            object_key = file_name.replace('+', '/')
            s3_client.upload_file(file_path, bucket_name, object_key)
            print(f"Uploaded {file_name} to {bucket_name}/{object_key}")
            os.remove(file_path)


def get_file_cnt(bucket_name: str, source_directory: str) -> int:
    """
    지정된 S3 버킷의 디렉토리에 있는 파일 수를 반환합니다.

    Args:
        bucket_name (str): S3 버킷 이름
        source_directory (str): 파일 수를 확인할 S3 디렉토리

    Returns:
        int: 디렉토리에 있는 파일 수. 오류가 발생할 경우 -1을 반환한다.
    """
    s3_client = get_s3_client()

    try:
        response = s3_client.list_objects_v2(
            Bucket=bucket_name, Prefix=source_directory)
        file_count = len(response.get('Contents', []))
        print(f"폴더 '{source_directory}'에 있는 파일 수: {file_count} 개")
        return file_count
    except Exception as e:
        print(f"오류 발생: {str(e)}")
        return -1
