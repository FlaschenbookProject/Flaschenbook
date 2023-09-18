resource "aws_iam_role" "ecs-task-execution-role" {
  assume_role_policy = jsonencode(
    {
      Statement = [
        {
          Action = "sts:AssumeRole"
          Effect = "Allow"
          Principal = {
            Service = "ecs-tasks.amazonaws.com"
          }
        },
      ]
      Version = "2012-10-17"
    }
  )
  description           = "Allows EC2 instances to call AWS ECS services on your behalf."
  force_detach_policies = false
  managed_policy_arns = [
    "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryFullAccess",
    "arn:aws:iam::aws:policy/AmazonEC2FullAccess",
    "arn:aws:iam::aws:policy/AmazonECS_FullAccess",
    "arn:aws:iam::aws:policy/AmazonElasticFileSystemFullAccess",
    "arn:aws:iam::aws:policy/EC2InstanceProfileForImageBuilderECRContainerBuilds",
    "arn:aws:iam::aws:policy/service-role/AmazonECSTaskExecutionRolePolicy",
  ]
  max_session_duration = 3600
  name                 = "ecsTaskExecutionRole"
  path                 = "/"
  tags                 = {}
  tags_all             = {}
}

#ecs-task-role
resource "aws_iam_policy" "ecs-task-policy" {
  name = "ecs-task-policy"
  path = "/"
  policy = jsonencode(
    {
      Statement = [
        {
          Action = [
            "ecr:GetAuthorizationToken",
            "ecr:BatchCheckLayerAvailability",
            "ecr:GetDownloadUrlForLayer",
            "ecr:BatchGetImage",
          ]
          Effect     = "Allow"
          "Resource" = "*"
        },
        {
          Action = [
            "logs:CreateLogStream",
            "logs:PutLogEvents",
          ]
          Effect     = "Allow"
          "Resource" = "*"
        },
        {
          Action = [
            "s3:*",
          ]
          Effect     = "Allow"
          "Resource" = "arn:aws:s3:::${var.bucket_name}/*"
          Sid        = "sid1"
        },
      ]
      Version = "2012-10-17"
    }
  )
  tags = {
    "Name" = "ecs-task-policy"
  }
  tags_all = {
    "Name" = "ecs-task-policy"
  }
}

resource "aws_iam_role" "ecs-task-role" {
  assume_role_policy = jsonencode(
    {
      Statement = [
        {
          Action = "sts:AssumeRole"
          Effect = "Allow"
          Principal = {
            Service = "ecs-tasks.amazonaws.com"
          }
          Sid = ""
        },
      ]
      Version = "2012-10-17"
    }
  )
  description           = "Allow ECS tasks to access AWS resources"
  force_detach_policies = false
  managed_policy_arns = [
    aws_iam_policy.ecs-task-policy.arn,
  ]
  max_session_duration = 3600
  name                 = "ecsTaskRole"
  path                 = "/"
  tags = {
    "Name"    = "ecs-task-role"
    "project" = var.environment
  }
  tags_all = {
    "Name"    = "ecs-task-role"
    "project" = var.environment
  }
}
