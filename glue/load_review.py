import sys
from awsglue.transforms import *
from awsglue.utils import getResolvedOptions
from pyspark.context import SparkContext
from awsglue.context import GlueContext
from awsglue.job import Job
from pyspark.sql import SparkSession
from awsglue.dynamicframe import DynamicFrame

# @params: [JOB_NAME]
args = getResolvedOptions(sys.argv, ['JOB_NAME'])

sc = SparkContext()
glueContext = GlueContext(sc)
spark = glueContext.spark_session
job = Job(glueContext)
job.init(args['JOB_NAME'], args)

mappings = [
    ("isbn", "string", "isbn", "string"),
    ("web_code", "string", "webCode", "string"),
    ("content", "string", "content", "string"),
    ("rating", "string", "rating", "decimal(3,1)"),
    ("wrt_date", "string", "wrtDate", "timestamp")]

# Load data from glue catalog
catalog_dyf = glueContext.create_dynamic_frame.from_catalog(
    database="flaschenbook-data-catalog-db",
    table_name="review",
    transformation_ctx="catalog_dyf"
)

mapped_catalog_dyf = ApplyMapping.apply(
    frame=catalog_dyf,
    mappings=mappings)

# Write the results back to flb-service-db-conn
dev_datasink = glueContext.write_dynamic_frame.from_jdbc_conf(
    frame=mapped_catalog_dyf,
    catalog_connection="flb-service-public-db-conn",
    connection_options={
        "dbtable": "BookReview",
        "database": "dev",
        "preactions": ["DELETE FROM dev.BookReview"]},
    transformation_ctx="dev_datasink")

prod_datasink = glueContext.write_dynamic_frame.from_jdbc_conf(
    frame=mapped_catalog_dyf,
    catalog_connection="flb-service-db-conn",
    connection_options={
        "dbtable": "BookReview",
        "database": "dev",
        "preactions": ["DELETE FROM dev.BookReview"]},
    transformation_ctx="prod_datasink")

job.commit()
