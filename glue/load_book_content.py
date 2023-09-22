import sys
from awsglue.transforms import *
from awsglue.utils import getResolvedOptions
from pyspark.context import SparkContext
from awsglue.context import GlueContext
from awsglue.job import Job
from pyspark.sql import SparkSession
from awsglue.dynamicframe import DynamicFrame
from pyspark.sql.functions import regexp_replace, length, trim, col

# @params: [JOB_NAME]
args = getResolvedOptions(sys.argv, ['JOB_NAME'])

sc = SparkContext()
glueContext = GlueContext(sc)
spark = glueContext.spark_session
job = Job(glueContext)
job.init(args['JOB_NAME'], args)

mappings = [("isbn", "string", "isbn", "string"),
            ("content", "string", "content", "string")]

# Load data from glue catalog
catalog_dyf = glueContext.create_dynamic_frame.from_catalog(
    database="flaschenbook-data-catalog-db",
    table_name="book_content",
    transformation_ctx="catalog_dyf"
)

mapped_catalog_dyf = ApplyMapping.apply(
    frame=catalog_dyf,
    mappings=mappings)

# Convert to DataFrame
mapped_catalog_df = mapped_catalog_dyf.toDF()
filtered_df = mapped_catalog_df.filter(length(trim(col('content'))) > 15)

# Remove unwanted text from content and filter out rows with content length <= 10
pattern = r'(p\.)|([0-9]+쪽)|([0-9]+ 쪽)|[<>()\[\]~_♣●〈]|(-[0-9]+쪽)|(-p\.[0-9]+)|(- p\.[0-9]+)|(-P\.[0-9]+)|(-~~에서)|(_[0-9]+쪽)|(/ p\.[0-9]+)|(_~~에서)|(_[0-9]+p)|(_ [0-9]+p)|(_[0-9]+p,)|(_ [0-9]+p,)|([0-9]+장 |)|(---p.[0-9])|(_[0-9]+쪽)|(- [0-9]+쪽)|(_[0-9]~[0-9]+쪽,)|(_[0-9]~[0-9]+쪽)'
filtered_df = filtered_df.withColumn('content', regexp_replace('content', pattern, '')).withColumn(
    'content', trim(col('content'))).filter(length('content') > 10)

# Convert back to DynamicFrame
filtered_dyf = DynamicFrame.fromDF(filtered_df, glueContext, "filtered_dyf")

# Write the results back to flb-service-db-conn
prod_datasink = glueContext.write_dynamic_frame.from_jdbc_conf(
    frame=filtered_dyf,
    catalog_connection="flb-service-db-conn",
    connection_options={
        "dbtable": "BookContent",
        "database": "dev",
        "preactions": ["DELETE FROM dev.BookContent"]},
    transformation_ctx="prod_datasink")

dev_datasink = glueContext.write_dynamic_frame.from_jdbc_conf(
    frame=filtered_dyf,
    catalog_connection="flb-service-public-db-conn",
    connection_options={
        "dbtable": "BookContent",
        "database": "dev",
        "preactions": ["DELETE FROM dev.BookContent"]},
    transformation_ctx="dev_datasink")

job.commit()
