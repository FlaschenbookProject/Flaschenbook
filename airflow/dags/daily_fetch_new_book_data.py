from datetime import datetime, timedelta
from dotenv import load_dotenv
from airflow.providers.docker.operators.docker import DockerOperator
# from airflow.providers.amazon.aws.sensors.s3 import S3KeySensor
from airflow.operators.python import PythonOperator
from base.base_dag import BaseDAG
from airflow.models import Variable
import os


def get_execution_date(**kwargs):
    execution_date = kwargs['execution_date']
    date = execution_date.strftime('%Y-%m-%d')
    print(f"{date} 실행")

    kwargs['ti'].xcom_push(key='TODAY', value=date)


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
        # bucket_name = Variable.get("bucket_name")
        environment = os.environ

        os.environ["NAVER_CLIENT_ID"] = Variable.get("naver_client_id")
        os.environ["NAVER_CLIENT_SECRET"] = Variable.get("naver_client_secret")
        os.environ["KAKAO_REST_API_KEY"] = Variable.get("kakao_rest_api_key")
        os.environ["TTB_KEY"] = Variable.get("ttb_api_key")

        execution_date_task = PythonOperator(
            task_id='execution_date_task',
            python_callable=get_execution_date,
            provide_context=True,
            dag=dag
        )

        # date = "{{ ti.xcom_pull(task_ids='get_execution_date', key='TODAY') }}"
        # object_key = f'raw/book_info/{site}/{date}/new.json'

        # container 이름이 중복되면 병렬 처리가 불가능
        fetch_api_data = DockerOperator(
            task_id='fetch_api_data',
            image=script_image,
            container_name=f'fetch_api_data_{site}',
            api_version='auto',
            auto_remove=True,
            command=["python", "get_api.py", "{{ ds }}", site],
            docker_url="unix://var/run/docker.sock",
            environment=environment
        )

        # check_file_exists = S3KeySensor(
        #     task_id='check_file_exists',
        #     bucket_key=f's3://{bucket_name}/{object_key}',
        #     aws_conn_id='aws_conn_id',
        #     timeout=18 * 60 * 60,
        #     poke_interval=10 * 60,
        #     mode='poke',
        #     poke_while_false=False,
        #     timeout_mode='poke'
        # )

        execution_date_task >> fetch_api_data 

    return dag


sites = ['aladin', 'kakao', 'naver']
for site in sites:
    dag_id = f'daily_fetch_new_book_{site}'
    globals()[dag_id] = create_fetch_new_book_dag(site)
