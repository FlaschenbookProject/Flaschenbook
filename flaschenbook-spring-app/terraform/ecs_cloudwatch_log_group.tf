resource "aws_cloudwatch_log_group" "ecs-flb-frontend" {
  name              = "/ecs/flb-frontend"
  retention_in_days = 0
  tags              = {}
  tags_all          = {}
}

resource "aws_cloudwatch_log_group" "ecs-flb-backend" {
  name              = "/ecs/flb-backend"
  retention_in_days = 0
  tags              = {}
  tags_all          = {}
}
