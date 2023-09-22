# ETL Jobs
resource "aws_s3_object" "load_book_content" {
  bucket = var.bucket_name
  key    = "glue/scripts/load_book_content.py"
  source = "${var.root_directory}/glue/load_book_content.py"
  etag   = filemd5("${var.root_directory}/glue/load_book_content.py")
}

resource "aws_s3_object" "load_book_detail" {
  bucket = var.bucket_name
  key    = "glue/scripts/load_book_detail.py"
  source = "${var.root_directory}/glue/load_book_detail.py"
  etag   = filemd5("${var.root_directory}/glue/load_book_detail.py")
}

resource "aws_s3_object" "load_book_info" {
  bucket = var.bucket_name
  key    = "glue/scripts/load_book_info.py"
  source = "${var.root_directory}/glue/load_book_info.py"
  etag   = filemd5("${var.root_directory}/glue/load_book_info.py")
}

resource "aws_s3_object" "load_review" {
  bucket = var.bucket_name
  key    = "glue/scripts/load_review.py"
  source = "${var.root_directory}/glue/load_review.py"
  etag   = filemd5("${var.root_directory}/glue/load_review.py")
}
