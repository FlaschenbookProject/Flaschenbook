locals {
  ecr_base_url = trimsuffix(aws_ecr_repository.flaschenbook-frontend.repository_url, "/flaschenbook-frontend")
}

# Frontend
resource "aws_codebuild_project" "flb-frontend-codebuild" {
  name          = "flb-frontend-codebuild"
  build_timeout = "5"
  service_role  = aws_iam_role.flb-codebuild_role.arn
  artifacts {
    type = "CODEPIPELINE"
  }

  environment {
    compute_type    = "BUILD_GENERAL1_SMALL"
    image           = "aws/codebuild/standard:5.0"
    type            = "LINUX_CONTAINER"
    privileged_mode = true
    environment_variable {
      name  = "REPOSITORY_NAME"
      value = aws_ecr_repository.flaschenbook-frontend.name
    }
    environment_variable {
      name  = "ECR_BASE_URL"
      value = local.ecr_base_url
    }
    environment_variable {
      name  = "REPOSITORY_URI"
      value = aws_ecr_repository.flaschenbook-frontend.repository_url
    }
    environment_variable {
      name  = "CONTAINER_NAME"
      value = "flb-frontend"
    }
  }

  source {
    type = "CODEPIPELINE"

    buildspec = <<-EOT
      version: 0.2
      phases:
        pre_build:
          commands:
            - echo "Logging in to Amazon ECR..."
            - aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $ECR_BASE_URL
        build:
          commands:
            - echo "Building the Docker image..."
            - docker build --no-cache -t $REPOSITORY_NAME .
            - docker tag $REPOSITORY_NAME:latest $REPOSITORY_URI:latest
        post_build:
          commands:
            - echo "Pushing the Docker image..."
            - docker push $REPOSITORY_URI:latest
            - echo '[{"name":"'$CONTAINER_NAME'","imageUri":"'$REPOSITORY_URI:latest'"}]' > imagedefinitions.json
            - cat imagedefinitions.json
      artifacts:
        files: 
          - imagedefinitions.json
    EOT
  }
}

resource "aws_codebuild_project" "flb-backend-codebuild" {
  name          = "flb-backend-codebuild"
  build_timeout = "5"
  service_role  = aws_iam_role.flb-codebuild_role.arn
  artifacts {
    type = "CODEPIPELINE"
  }

  environment {
    compute_type    = "BUILD_GENERAL1_SMALL"
    image           = "aws/codebuild/standard:5.0"
    type            = "LINUX_CONTAINER"
    privileged_mode = true
    environment_variable {
      name  = "REPOSITORY_NAME"
      value = aws_ecr_repository.flaschenbook-backend.name
    }
    environment_variable {
      name  = "ECR_BASE_URL"
      value = local.ecr_base_url
    }
    environment_variable {
      name  = "REPOSITORY_URI"
      value = aws_ecr_repository.flaschenbook-backend.repository_url
    }
    environment_variable {
      name  = "CONTAINER_NAME"
      value = "flb-backend"
    }
  }

  source {
    type = "CODEPIPELINE"

    buildspec = <<-EOT
      version: 0.2
      phases:
        pre_build:
          commands:
            - echo "Moving to the build directory from the root..."
            - cd flaschenbook-spring-app/flaschenbook/
            - echo "Current directory is:"
            - pwd
            - echo "Logging in to Amazon ECR..."
            - aws ecr get-login-password --region $AWS_DEFAULT_REGION | docker login --username AWS --password-stdin $ECR_BASE_URL
        build:
          commands:
            - echo "Building the Docker image..."
            - docker build --no-cache -t $REPOSITORY_NAME .
            - docker tag $REPOSITORY_NAME:latest $REPOSITORY_URI:latest
        post_build:
          commands:
            - echo "Pushing the Docker image..."
            - docker push $REPOSITORY_URI:latest
            - echo '[{"name":"'$CONTAINER_NAME'","imageUri":"'$REPOSITORY_URI:latest'"}]' > imagedefinitions.json
            - cat imagedefinitions.json
      artifacts:
        files: 
          - imagedefinitions.json
    EOT
  }
}
