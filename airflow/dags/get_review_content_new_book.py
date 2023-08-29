from datetime import datetime, timedelta
from dotenv import load_dotenv
from airflow.providers.docker.operators.docker import DockerOperator
from airflow.providers.amazon.aws.sensors.s3 import S3KeySensor
from base.base_dag import BaseDAG
from airflow.models import Variable
from airflow import macros
import os


def create_review_content_new_book_dag(site):
    with BaseDAG(
        dag_id=f'get_review_content_new_book_{site}',
        description=f'Get review and content Data from {site} scraping',
        schedule_interval=timedelta(days=1),
        catchup=False,
        start_date=datetime(2023, 8, 1)
    ) as dag:

        load_dotenv()
        # site가 kyobo면 scrap_kyobo.py
        # site가 aladin이면 scrap_aladin.py
        script_image = Variable.get("script_image")
        bucket_name = Variable.get("bucket_name")
        environment = os.environ

        # WEBCODE
        if site == "kyobo":
            WEBCODE = "KB"
        else:
            WEBCODE = "AL"

        ten_days_ago = macros.ds_add('{{ ds }}', -10)
        date = ten_days_ago.strftime("%Y-%m-%d")
        review_object_key = f'curated/reviews/{date}/new_reviews_{WEBCODE}.parquet'
        content_object_key = f'curated/book_content/{date}/new_book_contents.parquet'

        get_review_content = DockerOperator(
            task_id='get_review_content',
            image=script_image,
            container_name=f'get_review_content_{site}',
            api_version='auto',
            auto_remove=True,
            command=["python", f"scrap_{site}.py", date, WEBCODE],
            docker_url="unix://var/run/docker.sock",
            environment=environment
        )

        check_review_file_exists = S3KeySensor(
            task_id='check_review_file_exists',
            bucket_key=f's3://{bucket_name}/{review_object_key}',
            aws_conn_id='aws_conn_id',
            timeout=18 * 60 * 60,
            poke_interval=10 * 60,
        )

        check_content_file_exists = S3KeySensor(
            task_id='check_content_file_exists',
            bucket_key=f's3://{bucket_name}/{content_object_key}',
            aws_conn_id='aws_conn_id',
            timeout=18 * 60 * 60,
            poke_interval=10 * 60,
        )

        if site == "kyobo":
            get_review_content >> check_review_file_exists >> check_content_file_exists
        else:
            get_review_content >> check_review_file_exists

    return dag


sites = ['aladin', 'kyobo']
for site in sites:
    dag_id = f'get_review_content_new_book_{site}'
    globals()[dag_id] = create_review_content_new_book_dag(site)
