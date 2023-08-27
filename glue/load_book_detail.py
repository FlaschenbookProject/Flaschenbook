import sys
from awsglue.transforms import *
from awsglue.utils import getResolvedOptions
from pyspark.context import SparkContext
from awsglue.context import GlueContext
from awsglue.dynamicframe import DynamicFrame
from awsglue.job import Job

# @params: [JOB_NAME]
args = getResolvedOptions(sys.argv, ['JOB_NAME'])

sc = SparkContext()
glueContext = GlueContext(sc)
spark = glueContext.spark_session
job = Job(glueContext)
job.init(args['JOB_NAME'], args)
mappings = [("ISBN", "string", "ISBN", "string"),
            ("WEB_CODE", "string", "WEB_CODE", "string"),
            ("SALE_URL", "string", "SALE_URL", "string"),
            ("SALE_PRICE", "long", "SALE_PRICE", "decimal"),
            ("SALE_STATUS", "string", "SALE_STATUS", "string"),
            ("DESCRIPTION", "string", "DESCRIPTION", "string"),
            ("RANK", "string", "RANKING", "string")
            ]
datasource0 = glueContext.create_dynamic_frame.from_catalog(
    database="flaschenbook-data-catalog-db", table_name="book_detail", transformation_ctx="datasource0")
applymapping1 = ApplyMapping.apply(
    frame=datasource0, mappings=mappings, transformation_ctx="applymapping1")

# DynamicFrame을 DataFrame으로 변환
dataframe = applymapping1.toDF()

# DataFrame에서 중복된 ISBN 제거
deduplicated_df = dataframe.dropDuplicates(['ISBN', 'WEB_CODE'])

# DataFrame을 DynamicFrame으로 다시 변환
deduplicated = DynamicFrame.fromDF(
    deduplicated_df, glueContext, "deduplicated")

# deduplicated를 데이터베이스에 쓰기
datasink2 = glueContext.write_dynamic_frame.from_jdbc_conf(frame=deduplicated, catalog_connection="flb-service-db-conn", connection_options={
                                                           "dbtable": "BookDetail", "database": "dev"}, transformation_ctx="datasink2")

job.commit()
