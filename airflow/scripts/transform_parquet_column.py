import boto3
import pyarrow.parquet as pq
from io import BytesIO
import os
from dotenv import load_dotenv


def main():
    # AWS Credentials 설정
    access_key = os.environ.get("AWS_ACCESS_KEY_ID")
    secret_key = os.environ.get("AWS_SECRET_ACCESS_KEY")

    # S3 버킷 및 파일 경로 설정
    bucket_name = os.environ.get("BUCKET_NAME")
    prefix = 'curated/review/'

    # Boto3 client 생성
    s3 = boto3.client('s3', aws_access_key_id=access_key,
                      aws_secret_access_key=secret_key)

    # S3에서 Parquet 파일 목록 가져오기
    objects = s3.list_objects_v2(Bucket=bucket_name, Prefix=prefix)
    for obj in objects.get('Contents', []):
        file_key = obj['Key']
        print(file_key)
        if file_key.endswith('.parquet'):
            # Parquet 파일 읽기
            obj = s3.get_object(Bucket=bucket_name, Key=file_key)
            df = pq.read_table(BytesIO(obj['Body'].read())).to_pandas()

            # 컬럼 데이터형 변환
            # 예를 들어, 'rating' 컬럼의 데이터형을 double에서 integer로 변환
            df['rating'] = df['rating'].astype(str)

            # 결과를 같은 S3 경로에 저장
            output = BytesIO()
            df.to_parquet(output)
            s3.put_object(Bucket=bucket_name, Key=file_key,
                          Body=output.getvalue())
            print(f"{file_key} 변환하여 저장 완료")


if __name__ == "__main__":
    load_dotenv()
    main()
