resource "aws_codepipeline" "flb-frontend-pipeline" {
  name     = "flb-frontend-pipeline"
  role_arn = aws_iam_role.flb-codepipeline_role.arn

  artifact_store {
    type     = "S3"
    location = var.bucket_name
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
        ConnectionArn    = "${aws_codestarconnections_connection.flb-github-connection.arn}"
        FullRepositoryId = "FlaschenbookProject/Flaschenbook"
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
        ProjectName = "${aws_codebuild_project.flb-frontend-codebuild.name}"
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
        ServiceName = aws_ecs_service.flb-frontend-service.name
        FileName    = "imagedefinitions.json"
      }
    }
  }
}
