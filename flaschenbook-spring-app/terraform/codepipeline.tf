locals {
  pipelines = {
    frontend = {
      name               = "flb-frontend-pipeline",
      build_project_name = aws_codebuild_project.flb-frontend-codebuild.name,
      ecs_service_name   = aws_ecs_service.flb-frontend-service.name,
    },
    backend = {
      name               = "flb-backend-pipeline",
      build_project_name = aws_codebuild_project.flb-backend-codebuild.name,
      ecs_service_name   = aws_ecs_service.flb-backend-service.name,
    }
  }
}

resource "aws_codepipeline" "pipeline" {
  for_each = local.pipelines

  name     = each.value.name
  role_arn = aws_iam_role.flb-codepipeline_role.arn

  artifact_store {
    type     = "S3"
    location = var.codepipeline_bucket_name
  }

  stage {
    name = "Source"

    action {
      name             = "SourceAction"
      category         = "Source"
      owner            = "AWS"
      provider         = "CodeStarSourceConnection"
      version          = "1"
      output_artifacts = ["source_out"]

      configuration = {
        ConnectionArn    = aws_codestarconnections_connection.flb-github-connection.arn
        FullRepositoryId = var.repository_id
        BranchName       = "main"
      }
    }
  }

  stage {
    name = "Build"

    action {
      name             = "BuildAction"
      category         = "Build"
      owner            = "AWS"
      provider         = "CodeBuild"
      input_artifacts  = ["source_out"]
      output_artifacts = ["build_out"]
      version          = "1"

      configuration = {
        ProjectName = each.value.build_project_name
      }
    }
  }

  stage {
    name = "Deploy"

    action {
      name            = "DeployAction"
      category        = "Deploy"
      owner           = "AWS"
      provider        = "ECS"
      input_artifacts = ["build_out"]
      version         = "1"
      configuration = {
        ClusterName = aws_ecs_cluster.flb-ecs-cluster.name
        ServiceName = each.value.ecs_service_name
        FileName    = "imagedefinitions.json"
      }
    }
  }
}
