from datetime import datetime, timedelta
from dotenv import load_dotenv
from airflow.providers.docker.operators.docker import DockerOperator
from airflow.providers.amazon.aws.sensors.s3 import S3KeySensor
from base.base_dag import BaseDAG
from airflow.models import Variable
import os


def create_fetch_new_book_dag(site):
    with BaseDAG(
        dag_id=f'daily_fetch_new_book_{site}',
        description=f'Fetch data from {site} API',
        schedule_interval=timedelta(days=1),
        catchup=False,
        start_date=datetime(2023, 1, 1)
    ) as dag:

        load_dotenv()
        script_image = Variable.get("script_image")
        bucket_name = Variable.get("bucket_name")
        environment = os.environ
        date = os.environ["TODAY"] = '{{ ds }}'
        os.environ["BOOK_SITE"] = site
        object_key = f'raw/book_info/{site}/{date}/new.json'

        fetch_api_data = DockerOperator(
            task_id='fetch_api_data',
            image=script_image,
            container_name='fetch_api_data',
            api_version='auto',
            auto_remove=True,
            command="python get_api.py",
            docker_url="unix://var/run/docker.sock",
            environment=environment
        )

        check_file_exists = S3KeySensor(
            task_id='check_file_exists',
            bucket_key=f's3://{bucket_name}/{object_key}',
            aws_conn_id='aws_conn_id',
            timeout=18 * 60 * 60,
            poke_interval=10 * 60,
        )

        fetch_api_data >> check_file_exists

    return dag


sites = ['aladin', 'kakao', 'naver']
for site in sites:
    dag_id = f'daily_fetch_new_book_{site}'
    globals()[dag_id] = create_fetch_new_book_dag(site)
