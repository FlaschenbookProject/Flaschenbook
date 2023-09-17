# IAM role
resource "aws_iam_role" "glue_service_role" {
  name = "GlueServiceRole"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "glue.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "glue_service_s3_read" {
  role       = aws_iam_role.glue_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3ReadOnlyAccess"
}

resource "aws_iam_role_policy_attachment" "glue_service_rds_full_access" {
  role       = aws_iam_role.glue_service_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonRDSFullAccess"
}

# catalog db
resource "aws_glue_catalog_database" "aws_glue_catalog_database" {
  name = "flb-catalog-db"
}


# Crawler
resource "aws_glue_crawler" "flb-book-content-crawler" {
  database_name = aws_glue_catalog_database.aws_glue_catalog_database.name
  name          = "flb-book-content-crawler"
  role          = aws_iam_role.glue_service_role.arn

  s3_target {
    path = "s3://${var.bucket_name}/curated/book_content/"
  }
}

resource "aws_glue_crawler" "flb-book-detail-crawler" {
  database_name = aws_glue_catalog_database.aws_glue_catalog_database.name
  name          = "flb-book-detail-crawler"
  role          = aws_iam_role.glue_service_role.arn

  s3_target {
    path = "s3://${var.bucket_name}/curated/book_detail/"
  }
}

resource "aws_glue_crawler" "flb-book-info-crawler" {
  database_name = aws_glue_catalog_database.aws_glue_catalog_database.name
  name          = "flb-book-info-crawler"
  role          = aws_iam_role.glue_service_role.arn

  s3_target {
    path = "s3://${var.bucket_name}/curated/book_info/"
  }
}

resource "aws_glue_crawler" "flb-book-review-crawler" {
  database_name = aws_glue_catalog_database.aws_glue_catalog_database.name
  name          = "flb-book-review-crawler"
  role          = aws_iam_role.glue_service_role.arn

  s3_target {
    path = "s3://${var.bucket_name}/curated/review/"
  }
}
#

# db connection
resource "aws_security_group" "glue_sg" {
  name        = "GlueJDBCConnectionSG"
  description = "Security Group for AWS Glue to access RDS"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["${aws_subnet.private_subnet[0].cidr_block}"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_glue_connection" "flb-db-conn" {
  connection_properties = {
    JDBC_ENFORCE_SSL    = "false"
    JDBC_CONNECTION_URL = var.db_conn_url
    PASSWORD            = var.db_password
    USERNAME            = var.db_username
    KAFKA_SSL_ENABLED   = "false"
  }
  connection_type = "JDBC"
  name            = "flb-db-conn"

  physical_connection_requirements {
    availability_zone = aws_subnet.private_subnet[0].availability_zone
    security_group_id_list = [
      aws_security_group.glue_sg.id,
    ]
    subnet_id = aws_subnet.private_subnet[0].id
  }
}

# ETL Jobs
resource "aws_s3_bucket_object" "script" {
  bucket = var.bucket_name
  key    = "path/to/script.py"
  source = "local/path/to/script.py" # 로컬에서의 스크립트 파일 위치
  etag   = filemd5("local/path/to/script.py")
}


