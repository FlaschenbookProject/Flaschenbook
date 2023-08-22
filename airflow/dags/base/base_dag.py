from datetime import timedelta
from airflow import DAG


class BaseDAG(DAG):

    def __init__(self, *args, **kwargs):
        # 공통된 기본값을 설정
        default_args = {
            'owner': 'flaschenbook',
            'depends_on_past': False,
            'email_on_failure': False,
            'email_on_retry': False,
            'retries': 1,
            'retry_delay': timedelta(minutes=5),
        }

        # 기본값이 주어진 경우엔, 공통된 기본값과 병합
        if 'default_args' in kwargs:
            default_args.update(kwargs['default_args'])
            kwargs['default_args'] = default_args
        else:
            kwargs['default_args'] = default_args

        # DAG 클래스의 초기화 메서드 호출
        super().__init__(*args, **kwargs)
