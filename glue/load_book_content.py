import sys
from awsglue.transforms import *
from awsglue.utils import getResolvedOptions
from pyspark.context import SparkContext
from awsglue.context import GlueContext
from awsglue.job import Job


# @params: [JOB_NAME]
args = getResolvedOptions(sys.argv, ['JOB_NAME'])

sc = SparkContext()
glueContext = GlueContext(sc)
spark = glueContext.spark_session
job = Job(glueContext)
job.init(args['JOB_NAME'], args)
mappings = [("isbn", "string", "isbn", "string"),
            ("content", "string", "content", "string")]
datasource0 = glueContext.create_dynamic_frame.from_catalog(
    database="flaschenbook-data-catalog-db", table_name="book_content", transformation_ctx="datasource0")
applymapping1 = ApplyMapping.apply(
    frame=datasource0, mappings=mappings, transformation_ctx="applymapping1")
datasink2 = glueContext.write_dynamic_frame.from_jdbc_conf(frame=applymapping1, catalog_connection="flb-service-db-conn", connection_options={
                                                           "dbtable": "BookContent", "database": "dev"}, transformation_ctx="datasink2")


job.commit()