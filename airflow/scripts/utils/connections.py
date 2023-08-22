import boto3
import os


def get_s3_client() -> boto3.client:
    """
    Amazon S3 클라이언트 객체를 반환합니다.

    Returns:
        boto3.client: boto3 S3 클라이언트 객체
    """
    s3_client = boto3.client(
        's3',
        aws_access_key_id=os.environ.get("AWS_ACCESS_KEY_ID"),
        aws_secret_access_key=os.environ.get("AWS_SECRET_ACCESS_KEY")
    )

    return s3_client
