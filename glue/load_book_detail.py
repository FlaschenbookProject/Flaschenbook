import sys
from awsglue.transforms import *
from awsglue.utils import getResolvedOptions
from pyspark.context import SparkContext
from awsglue.context import GlueContext
from awsglue.dynamicframe import DynamicFrame
from awsglue.job import Job
from pyspark.sql.functions import length


# @params: [JOB_NAME]
args = getResolvedOptions(sys.argv, ['JOB_NAME'])

sc = SparkContext()
glueContext = GlueContext(sc)
spark = glueContext.spark_session
job = Job(glueContext)
job.init(args['JOB_NAME'], args)
mappings = [("ISBN", "string", "isbn", "string"),
            ("WEB_CODE", "string", "webCode", "string"),
            ("SALE_URL", "string", "saleUrl", "string"),
            ("SALE_PRICE", "long", "salePrice", "int"),
            ("SALE_STATUS", "string", "saleStatus", "string"),
            ("DESCRIPTION", "string", "description", "string"),
            ("RANK", "string", "ranking", "string")
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
    table_name="book_detail",
    transformation_ctx="catalog_dyf"
)

mapped_catalog_dyf = ApplyMapping.apply(
    frame=catalog_dyf,
    mappings=mappings
)


# DynamicFrame을 DataFrame으로 변환
dataframe = mapped_catalog_dyf.toDF()
dataframe_without_duplicates = dataframe.dropDuplicates(['isbn', 'webCode'])


# Select only the ISBNs and webCodes from the prod_db
prod_db_keys = prod_db_df.select('isbn', 'webCode')

# Remove the rows with ISBNs and webCodes that are already in the prod_db
new_data = dataframe_without_duplicates.join(
    prod_db_keys, ['isbn', 'webCode'], 'left_anti')

new_data = DynamicFrame.fromDF(new_data, glueContext, "new_data")

# Filter rows where ISBN length is greater than 13
too_long_isbn_df = new_data.toDF().filter(length("isbn") > 13)

# Print rows with ISBN length greater than 13
too_long_isbn_df.show()

# Remove rows with ISBN length greater than 13
filtered_new_data = new_data.toDF().filter(length("isbn") <= 13)

# Convert filtered DataFrame back to DynamicFrame
new_dyf = DynamicFrame.fromDF(filtered_new_data, glueContext, "new_dyf")


# Write the results
prod_datasink = glueContext.write_dynamic_frame.from_jdbc_conf(
    frame=new_dyf,
    catalog_connection="flb-service-db-conn",
    connection_options={"dbtable": "BookDetail", "database": "dev"},
    transformation_ctx="prod_datasink")

dev_datasink = glueContext.write_dynamic_frame.from_jdbc_conf(
    frame=new_dyf,
    catalog_connection="flb-service-public-db-conn",
    connection_options={"dbtable": "BookDetail", "database": "dev"},
    transformation_ctx="dev_datasink")

job.commit()
