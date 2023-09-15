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

mappings = [("ISBN", "string", "isbn", "string"),
            ("TITLE", "string", "title", "string"),
            ("CATEGORY_ID", "bigint", "categoryId", "int"),
            ("AUTHOR", "string", "author", "string"),
            ("TRANSLATOR", "string", "translator", "string"),
            ("PUBLISHER", "string", "publisher", "string"),
            ("PUBDATE", "string", "pubDate", "timestamp"),
            ("PRICE", "bigint", "price", "decimal(10,0)"),
            ("PAGE_CNT", "bigint", "pageCnt", "int"),
            ("IMAGE_URL", "string", "imageUrl", "string")
            ]

# Load data from RDS DB
prod_db_df = spark.read.jdbc(
    url="jdbc:mysql://db-endpoint:3306/database-name",
    table="BookDetail",
    properties={
        "user": "username",
        "password": "password"
    }
)


# Load data from glue catalog
catalog_dyf = glueContext.create_dynamic_frame.from_catalog(
    database="flaschenbook-data-catalog-db",
    table_name="book_info",
    transformation_ctx="catalog_dyf"
)

mapped_catalog_dyf = ApplyMapping.apply(
    frame=catalog_dyf,
    mappings=mappings
)

# DynamicFrame을 DataFrame으로 변환
dataframe = mapped_catalog_dyf.toDF()
dataframe_without_duplicates = dataframe.dropDuplicates(['isbn'])

# Select only the ISBNs from the prod_db
prod_db_isbns = prod_db_df.select('isbn')

# Remove the rows with ISBNs that are already in the prod_db
new_data = dataframe_without_duplicates.join(
    prod_db_isbns, ['isbn'], 'left_anti')

# Convert back to DynamicFrame
new_dyf = DynamicFrame.fromDF(new_data, glueContext, "new_dyf")

# Write the results
prod_datasink = glueContext.write_dynamic_frame.from_jdbc_conf(
    frame=new_dyf,
    catalog_connection="flb-service-db-conn",
    connection_options={"dbtable": "BookInfo",
                        "database": "dev", "overwrite": "true"},
    transformation_ctx="prod_datasink")

dev_datasink = glueContext.write_dynamic_frame.from_jdbc_conf(
    frame=new_dyf,
    catalog_connection="flb-service-public-db-conn",
    connection_options={"dbtable": "BookInfo", "database": "dev"},
    transformation_ctx="dev_datasink")

job.commit()
