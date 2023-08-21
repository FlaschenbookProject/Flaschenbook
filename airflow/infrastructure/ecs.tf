resource "aws_ecr_repository" "docker_repository" {
    name = "${var.project_name}-${var.stage}"
  tags = {
    Name = "${var.project_name}-${var.stage}-aws_ecr_repository-docker_repository"
  }
}

resource "aws_ecr_lifecycle_policy" "docker_repository_lifecycle" {
  repository = aws_ecr_repository.docker_repository.name

  policy = <<EOF
{
    "rules": [
        {
            "rulePriority": 1,
            "description": "Keep only the latest 5 images",
            "selection": {
                "tagStatus": "any",
                "countType": "imageCountMoreThan",
                "countNumber": 5
            },
            "action": {
                "type": "expire"
            }
        }
    ]
}
EOF
}

resource "aws_ecs_cluster" "ecs_cluster" {
  name = "${var.project_name}-${var.stage}"
  tags = {
    Name = "${var.project_name}-${var.stage}-ecs_cluster"
  }
}

resource "aws_cloudwatch_log_group" "log_group" {
  name = "${var.log_group_name}/${var.project_name}-${var.stage}"
  retention_in_days = 5
}

resource "aws_iam_role" "ecs_task_iam_role" {
  name = "${var.project_name}-${var.stage}-ecs-task-role"
  description = "Allow ECS tasks to access AWS resources"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "",
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
EOF
  tags = {
    Name = "${var.project_name}-${var.stage}-iam-role",
    project = "${var.project_name}"
  }
}


resource "aws_iam_policy" "ecs_task_policy" {
  name        = "${var.project_name}-${var.stage}"

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:BatchGetImage"
      ],
      "Effect": "Allow",
      "Resource": "*"
    },
    {
      "Action": [
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Effect": "Allow",
      "Resource": "*"
    },
    {
      "Sid": "sid1",
      "Effect": "Allow",
      "Action": [
          "s3:*"
      ],
      "Resource": "arn:aws:s3:::${var.s3_bucket}/*"
    },
    {
      "Sid": "sid2",
      "Effect": "Allow",
      "Action": [
          "s3:*"
      ],
      "Resource": "arn:aws:s3:::${var.s3_bucket}"
    }
  ]
}
EOF
tags = {
    Name = "${var.project_name}-${var.stage}-iam-policy"
  }
}

resource "aws_iam_role_policy_attachment" "attach_policy" {
  role       = aws_iam_role.ecs_task_iam_role.name
  policy_arn = aws_iam_policy.ecs_task_policy.arn
}
