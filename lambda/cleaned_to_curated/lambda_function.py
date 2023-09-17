import boto3
import re
import pandas as pd
from io import BytesIO

s3 = boto3.client('s3')


def lambda_handler(event, context):
    try:
        bucket = event['Records'][0]['s3']['bucket']['name']
        cleaned_key = event['Records'][0]['s3']['object']['key']

        if re.match(r'cleaned/book_info/.*/\d{4}-\d{2}-\d{2}/init/.*.parquet', cleaned_key):
            date = cleaned_key.split('/')[3]
            book_type = cleaned_key.split('/')[4].replace(".parquet", "")
            book_type = 'init'
            book_num = cleaned_key.split(
                '/')[-1].split('init/')[-1].replace('.parquet', '')

            keys = [
                f'cleaned/book_info/naver/{date}/{book_type}/{book_num}.parquet',
                f'cleaned/book_info/kakao/{date}/{book_type}/{book_num}.parquet',
                f'cleaned/book_info/aladin/{date}/{book_type}/{book_num}.parquet',
            ]
            book_info_key = f'curated/book_info/{date}_{book_type}_{book_num}.parquet'
            book_detail_key = f'curated/book_detail/{date}_{book_type}_{book_num}.parquet'

            # S3에서 parquet 파일 읽기
            def read_parquet_from_s3(bucket, key):
                try:
                    response = s3.get_object(Bucket=bucket, Key=key)
                    data = response['Body'].read()
                    print(key)
                    print(data[1:10])
                    return pd.read_parquet(BytesIO(data))
                except s3.exceptions.NoSuchKey:
                    return None

            dfs = [read_parquet_from_s3(bucket, key) for key in keys]

            # 모든 파일이 존재하는지 확인
            if any(df is None or df.empty for df in dfs):
                return {
                    'statusCode': 400,
                    'body': 'One or more files do not exist!'
                }

            print("df 정의")
            # df 정의
            df_naver = dfs[0]
            df_kakao = dfs[1]
            df_aladin = dfs[2]
            print(len(df_naver))
            print(len(df_kakao))
            print(len(df_aladin))

            if any(df is None or df.empty for df in dfs):
                return {
                    'statusCode': 400,
                    'body': 'One or more files do not exist!'
                }

            print("book_info 생성")
            # 1. book_info 생성
            # 데이터 프레임에서 필요한 정보 선택
            df_naver_selected = df_naver[['ISBN', 'IMAGE_URL']]
            df_kakao_selected = df_kakao[['ISBN', 'AUTHOR', 'TRANSLATOR']]
            df_aladin_selected = df_aladin[[
                'ISBN', 'TITLE', 'CATEGORY_ID', 'PUBLISHER', 'PUBDATE', 'PRICE', 'PAGE_CNT']]

            print("선택된 열들로 새로운 데이터프레임 생성")
            # 선택된 열들로 새로운 데이터프레임 생성
            df_selected = pd.merge(pd.merge(
                df_naver_selected, df_kakao_selected, on='ISBN'), df_aladin_selected, on='ISBN')
            print(f'{len(df_selected)} 개 ')

            # parquet 파일로 변환
            parquet_buffer = BytesIO()
            df_selected.to_parquet(parquet_buffer)

            # 변환된 데이터를 S3에 저장
            s3.put_object(Bucket=bucket, Key=book_info_key,
                          Body=parquet_buffer.getvalue())

            # 2. book_detail 생성
            header = ['ISBN', 'WEB_CODE', 'SALE_URL',
                      'SALE_PRICE', 'SALE_STATUS', 'DESCRIPTION', 'RANK']

            # 주어진 헤더의 각 열에 대해, 해당 열이 데이터 프레임에 존재하는지 확인하고,
            # 존재하지 않으면 NaN 값을 포함하는 열을 추가하고, WEB_CODE 열의 값을 설정
            def select_columns(df, header, web_code=None):
                for col in header:
                    if col not in df.columns:
                        df[col] = None
                if web_code:
                    df['WEB_CODE'] = web_code
                if 'SALE_PRICE' in df.columns and df['SALE_PRICE'].dtype == 'object':
                    df['SALE_PRICE'] = df['SALE_PRICE'].str.replace(
                        ',', '').astype(int)
                return df[header]

            # 각 데이터 프레임에서 주어진 헤더의 열만 선택
            df_naver_selected = select_columns(df_naver, header, web_code='NA')
            df_kakao_selected = select_columns(df_kakao, header, web_code='KK')
            df_aladin_selected = select_columns(
                df_aladin, header, web_code='AL')

            # 선택된 열들로 새로운 데이터프레임 생성
            df_selected = pd.concat(
                [df_naver_selected, df_kakao_selected, df_aladin_selected])

            # parquet 파일로 변환
            parquet_buffer = BytesIO()
            df_selected.to_parquet(parquet_buffer)

            # 변환된 데이터를 S3에 저장
            s3.put_object(Bucket=bucket, Key=book_detail_key,
                          Body=parquet_buffer.getvalue())

            return {
                'statusCode': 200,
                'body': 'Parquet file created successfully!'
            }
        else:
            print(
                f"Object key {cleaned_key} does not match the expected pattern.")
            return {
                'statusCode': 500,
                'body': f"Object doesn't match the expected pattern: {cleaned_key}"
            }
    except Exception as e:
        return {
            'statusCode': 500,
            'body': f'Failure: {str(e)}'
        }
