# Python 공식 이미지를 베이스로 사용
FROM python:3.8-slim

# 필요한 패키지 설치
RUN pip install requests pandas python-dotenv boto3 urllib3 typing playwright s3fs==2023.1.0 nest_asyncio

# 작업 디렉터리 설정
WORKDIR /app

# 코드 추가
COPY . .

