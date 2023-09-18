resource "aws_glue_job" "load_book_content" {
  connections = [
    "flb-db-conn",
  ]
  default_arguments = {
    "--enable-continuous-cloudwatch-log" = "true"
    "--enable-glue-datacatalog"          = "true"
    "--enable-job-insights"              = "false"
    "--enable-metrics"                   = "true"
    "--enable-spark-ui"                  = "true"
    "--job-bookmark-option"              = "job-bookmark-disable"
    "--job-language"                     = "python"
    "--TempDir"                          = "s3://${var.bucket_name}/glue/temporary/"
    "--spark-event-logs-path"            = "s3://${var.bucket_name}/glue/sparkHistoryLogs/"
  }
  execution_class           = "STANDARD"
  glue_version              = "4.0"
  max_capacity              = 2
  max_retries               = 0
  name                      = "load_book_content"
  non_overridable_arguments = {}
  role_arn                  = aws_iam_role.glue_service_role.arn
  tags                      = {}
  tags_all                  = {}
  timeout                   = 2880

  command {
    name            = "glueetl"
    python_version  = "3"
    script_location = "s3://${var.bucket_name}/glue/scripts/load_book_content.py"
  }

  execution_property {
    max_concurrent_runs = 1
  }
}

resource "aws_glue_job" "load_book_info" {
  connections = [
    "flb-db-conn",
  ]
  default_arguments = {
    "--enable-continuous-cloudwatch-log" = "true"
    "--enable-glue-datacatalog"          = "true"
    "--enable-job-insights"              = "false"
    "--enable-metrics"                   = "true"
    "--enable-spark-ui"                  = "true"
    "--job-bookmark-option"              = "job-bookmark-disable"
    "--job-language"                     = "python"
    "--TempDir"                          = "s3://${var.bucket_name}/glue/temporary/"
    "--spark-event-logs-path"            = "s3://${var.bucket_name}/glue/sparkHistoryLogs/"
  }
  execution_class           = "STANDARD"
  glue_version              = "4.0"
  max_capacity              = 2
  max_retries               = 0
  name                      = "load_book_info"
  non_overridable_arguments = {}
  role_arn                  = aws_iam_role.glue_service_role.arn
  tags                      = {}
  tags_all                  = {}
  timeout                   = 2880

  command {
    name            = "glueetl"
    python_version  = "3"
    script_location = "s3://${var.bucket_name}/glue/scripts/load_book_info.py"
  }

  execution_property {
    max_concurrent_runs = 1
  }
}

resource "aws_glue_job" "load_book_detail" {
  connections = [
    "flb-db-conn",
  ]
  default_arguments = {
    "--enable-continuous-cloudwatch-log" = "true"
    "--enable-glue-datacatalog"          = "true"
    "--enable-job-insights"              = "false"
    "--enable-metrics"                   = "true"
    "--enable-spark-ui"                  = "true"
    "--job-bookmark-option"              = "job-bookmark-disable"
    "--job-language"                     = "python"
    "--TempDir"                          = "s3://${var.bucket_name}/glue/temporary/"
    "--spark-event-logs-path"            = "s3://${var.bucket_name}/glue/sparkHistoryLogs/"
  }
  execution_class           = "STANDARD"
  glue_version              = "4.0"
  max_capacity              = 2
  max_retries               = 0
  name                      = "load_book_detail"
  non_overridable_arguments = {}
  role_arn                  = aws_iam_role.glue_service_role.arn
  tags                      = {}
  tags_all                  = {}
  timeout                   = 2880

  command {
    name            = "glueetl"
    python_version  = "3"
    script_location = "s3://${var.bucket_name}/glue/scripts/load_book_detail.py"
  }

  execution_property {
    max_concurrent_runs = 1
  }
}

resource "aws_glue_job" "load_review" {
  connections = [
    "flb-db-conn",
  ]
  default_arguments = {
    "--enable-continuous-cloudwatch-log" = "true"
    "--enable-glue-datacatalog"          = "true"
    "--enable-job-insights"              = "false"
    "--enable-metrics"                   = "true"
    "--enable-spark-ui"                  = "true"
    "--job-bookmark-option"              = "job-bookmark-disable"
    "--job-language"                     = "python"
    "--TempDir"                          = "s3://${var.bucket_name}/glue/temporary/"
    "--spark-event-logs-path"            = "s3://${var.bucket_name}/glue/sparkHistoryLogs/"
  }
  execution_class           = "STANDARD"
  glue_version              = "4.0"
  max_capacity              = 2
  max_retries               = 0
  name                      = "load_review"
  non_overridable_arguments = {}
  role_arn                  = aws_iam_role.glue_service_role.arn
  tags                      = {}
  tags_all                  = {}
  timeout                   = 2880

  command {
    name            = "glueetl"
    python_version  = "3"
    script_location = "s3://${var.bucket_name}/glue/scripts/load_review.py"
  }

  execution_property {
    max_concurrent_runs = 1
  }
}
