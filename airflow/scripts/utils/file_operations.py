from utils.connections import get_s3_client
import os
import glob
import pandas as pd


# csv를 저장
def save_to_csv(isbn_list, filename):
    df = pd.DataFrame({"ISBN": isbn_list})  # 데이터프레임 생성
    df.to_csv(filename, index=False)


def upload_files_to_s3(bucket_name, source_directory):
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
  
                      
def get_file_cnt(bucket_name, source_directory):
    s3_client = get_s3_client()
    
    try:
        response = s3_client.list_objects_v2(Bucket=bucket_name, Prefix=source_directory)
        file_count = len(response.get('Contents', []))
        print(f"폴더 '{source_directory}'에 있는 파일 수: {file_count} 개")
        return file_count
    except Exception as e:
        print(f"오류 발생: {str(e)}")
        return -1