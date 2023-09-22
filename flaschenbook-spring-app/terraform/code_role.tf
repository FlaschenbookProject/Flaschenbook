# IAM Role for CodeBuild
resource "aws_iam_role" "flb-codebuild_role" {
  name = "flb-codebuild-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "codebuild.amazonaws.com"
        }
      }
    ]
  })
}
resource "aws_iam_policy" "ecr_policy" {
  name        = "ecr_policy"
  description = "Policy to allow CodeBuild to access ECR"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = [
          "ecr:GetDownloadUrlForLayer",
          "ecr:BatchGetImage",
          "ecr:BatchCheckLayerAvailability",
          "ecr:PutImage",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:GetAuthorizationToken",
          "ecr:CreateRepository",
          "codebuild:BatchGetBuilds"
        ],
        Effect   = "Allow",
        Resource = "*"
      }
    ]
  })
}
resource "aws_iam_role_policy_attachment" "codebuild_cloudwatch_policy_attachment" {
  role       = aws_iam_role.flb-codebuild_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchFullAccess"
}
resource "aws_iam_role_policy_attachment" "codebuild_s3_policy_attachment" {
  role       = aws_iam_role.flb-codebuild_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}
resource "aws_iam_role_policy_attachment" "codebuild_ecr_policy_attachment" {
  policy_arn = aws_iam_policy.ecr_policy.arn
  role       = aws_iam_role.flb-codebuild_role.name
}


resource "aws_iam_policy" "codestar_connections_policy" {
  name        = "codestar-connections-policy"
  description = "Policy to allow usage of CodeStar Connection"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect   = "Allow",
        Action   = "codestar-connections:UseConnection",
        Resource = "${aws_codestarconnections_connection.flb-github-connection.arn}"
      }
    ]
  })
}
resource "aws_iam_policy" "codebuild_startbuild_policy" {
  name        = "codebuild-startbuild-policy"
  description = "Policy to allow starting CodeBuild projects"

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "codebuild:StartBuild",
          "codebuild:BatchGetBuilds"
        ]
        Resource = "*"
      }
    ]
  })
}


resource "aws_iam_role" "flb-codepipeline_role" {
  name = "flb-codepipeline-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "codepipeline.amazonaws.com"
        }
      }
    ]
  })
}
resource "aws_iam_role_policy_attachment" "codepipeline_policy_attachment" {
  role       = aws_iam_role.flb-codepipeline_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSCodePipeline_FullAccess"
}
resource "aws_iam_role_policy_attachment" "s3_policy_attachment" {
  role       = aws_iam_role.flb-codepipeline_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonS3FullAccess"
}
resource "aws_iam_role_policy_attachment" "codestar_connections_policy_attachment" {
  role       = aws_iam_role.flb-codepipeline_role.name
  policy_arn = aws_iam_policy.codestar_connections_policy.arn
}
resource "aws_iam_role_policy_attachment" "codebuild_policy_attachment" {
  role       = aws_iam_role.flb-codepipeline_role.name
  policy_arn = aws_iam_policy.codebuild_startbuild_policy.arn
}
resource "aws_iam_role_policy_attachment" "cloudwatch_policy_attachment" {
  role       = aws_iam_role.flb-codepipeline_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchFullAccess"
}
resource "aws_iam_role_policy_attachment" "codedeploy_policy_attachment" {
  role       = aws_iam_role.flb-codepipeline_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSCodeDeployFullAccess"
}
resource "aws_iam_role_policy_attachment" "ecs_policy_attachment" {
  role       = aws_iam_role.flb-codepipeline_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonECS_FullAccess"
}

resource "aws_iam_role" "flb-codedeploy_role" {
  name = "flb-codedeploy_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Principal = {
          Service = "codedeploy.amazonaws.com"
        }
      }
    ]
  })
}
resource "aws_iam_role_policy_attachment" "codedeploy_policy_attachment1" {
  role       = aws_iam_role.flb-codedeploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSCodeDeployFullAccess"
}
resource "aws_iam_role_policy_attachment" "codedeploy_policy_attachment2" {
  role       = aws_iam_role.flb-codedeploy_role.name
  policy_arn = "arn:aws:iam::aws:policy/AWSCodeDeployRoleForECS"
}
