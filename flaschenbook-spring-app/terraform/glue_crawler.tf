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
