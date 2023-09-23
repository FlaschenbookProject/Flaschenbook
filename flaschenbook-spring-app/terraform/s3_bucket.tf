resource "aws_s3_bucket" "flb-codepipeline-artifacts" {
  bucket = var.codepipeline_bucket_name

  tags = {
    Name = "CodePipeline Artifacts"
  }
}

resource "aws_s3_bucket_lifecycle_configuration" "flb-codepipeline-artifacts-lifecycle" {
  bucket = aws_s3_bucket.flb-codepipeline-artifacts.bucket

  rule {
    id = "github_artifacts"
    expiration {
      days = 30
    }
    status = "Enabled"
  }
}
