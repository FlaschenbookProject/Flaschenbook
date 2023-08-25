import boto3
import pandas as pd
import re
from io import BytesIO
from transform_data import transform_data_naver
from transform_data import transform_data_kakao
from transform_data import transform_data_aladin


def lambda_handler(event, context):
    try:
        s3 = boto3.client('s3')

        # 이벤트로부터 S3 버킷과 객체 키 정보 추출
        bucket = event['Records'][0]['s3']['bucket']['name']
        raw_key = event['Records'][0]['s3']['object']['key']
        print(bucket)
        print(raw_key)

        # 키가 'raw/book_info/<날짜>/new.json' 패턴에 맞는지 확인
        if re.match(r'raw/book_info/.*/\d{4}-\d{2}-\d{2}/.*.json', raw_key):

            # S3에서 raw 데이터 가져오기
            raw_object = s3.get_object(Bucket=bucket, Key=raw_key)
            raw_content = raw_object['Body'].read().decode('utf-8')
            site = raw_key.split('/')[2]

            transformed_data = None

            if site == 'naver':
                transformed_data = transform_data_naver(raw_content)
            elif site == 'kakao':
                transformed_data = transform_data_kakao(raw_content)
            elif site == 'aladin':
                transformed_data = transform_data_aladin(raw_content)

            # parquet로 변환
            df = pd.DataFrame(transformed_data)
            buffer = BytesIO()
            df.to_parquet(buffer, engine='pyarrow', index=False)

            # 변환된 데이터를 cleaned/ 경로에 저장
            cleaned_key = raw_key.replace(
                'raw/', 'cleaned/').replace('.json', '.parquet')
            s3.put_object(Bucket=bucket, Key=cleaned_key,
                          Body=buffer.getvalue())

            print(
                f"Transformed data from {raw_key} and saved to {cleaned_key}")
            return {
                'statusCode': 200,
                'body': 'Success: Data transformed and saved successfully!'
            }

        else:
            print(f"Object key {raw_key} does not match the expected pattern.")
            return {
                'statusCode': 500,
                'body': f"Object doesn't match the expected pattern: {raw_key}"
            }

    except Exception as e:
        return {
            'statusCode': 500,
            'body': f'Failure: {str(e)}'
        }
