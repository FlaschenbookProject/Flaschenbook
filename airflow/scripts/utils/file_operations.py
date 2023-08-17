from utils.connetions import get_s3_client
import os
import glob


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
