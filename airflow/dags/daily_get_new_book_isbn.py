from datetime import datetime, timedelta
from dotenv import load_dotenv
from airflow import DAG
from airflow.providers.docker.operators.docker import DockerOperator
from airflow.operators.python import PythonOperator
from airflow.operators.trigger_dagrun import TriggerDagRunOperator
from airflow.providers.amazon.aws.sensors.s3 import S3KeySensor
from airflow.models import Variable
import os


default_args = {
    'owner': 'song',
    'depends_on_past': False,
    'retries': 1,
    'retry_delay': timedelta(minutes=5),
}


def get_execution_date(**kwargs):
    # execution_date 조회
    execution_date = kwargs['execution_date']
    date = execution_date.strftime('%Y-%m-%d')
    print(f"{date} 실행")

    # 현재 날짜를 XCom을 통해 저장
    kwargs['ti'].xcom_push(key='TODAY', value=date)


def create_docker_task(task_id, command):
    script_image = Variable.get("script_image")
    environment = os.environ

    return DockerOperator(
        task_id=task_id,
        image=script_image,
        container_name=task_id,
        api_version='auto',
        auto_remove=True,
        command=command,
        docker_url="unix://var/run/docker.sock",
        environment=environment
    )


with DAG(
        dag_id='daily_get_new_book_isbn',
        description='Fetch new book isbn data from API',
        schedule_interval=timedelta(days=1),
        start_date=datetime(2023, 8, 1),
        catchup=False
) as dag:

    load_dotenv()

    bucket_name = Variable.get("bucket_name")
    os.environ["TTB_KEY"] = Variable.get("ttb_api_key")

    get_execution_date = PythonOperator(
        task_id='get_execution_date',
        python_callable=get_execution_date,
        provide_context=True,
        dag=dag
    )

    date = "{{ ti.xcom_pull(task_ids='get_execution_date', key='TODAY') }}"
    object_key = f'raw/isbn/{date}/new.csv'
    print(f"object_key: {object_key}")

    get_isbn_data = create_docker_task(
        task_id='get_isbn_data',
        command=["python", "get_api_new_isbn.py", date]
    )

    check_file_exists = S3KeySensor(
        task_id='check_file_exists',
        bucket_key=f's3://{bucket_name}/{object_key}',
        aws_conn_id='aws_conn_id',
        timeout=18 * 60 * 60,
        poke_interval=10 * 60,
        dag=dag
    )

    check_file_exists.log.info(f'Checking for file: {f"s3://{bucket_name}/{object_key}"}')

    # 'daily_fetch_new_book_naver' DAG 트리거
    trigger_naver_dag = TriggerDagRunOperator(
        task_id='trigger_daily_fetch_new_book_naver',
        trigger_dag_id='daily_fetch_new_book_naver',
        dag=dag,
    )

    # 'daily_fetch_new_book_kakao' DAG 트리거
    trigger_kakao_dag = TriggerDagRunOperator(
        task_id='trigger_daily_fetch_new_book_kakao',
        trigger_dag_id='daily_fetch_new_book_kakao',
        dag=dag,
    )

    # 'daily_fetch_new_book_aladin' DAG 트리거
    trigger_aladin_dag = TriggerDagRunOperator(
        task_id='trigger_daily_fetch_new_book_aladin',
        trigger_dag_id='daily_fetch_new_book_aladin',
        dag=dag,
    )

    get_execution_date >> get_isbn_data >> check_file_exists >> [trigger_naver_dag, trigger_kakao_dag, trigger_aladin_dag]
