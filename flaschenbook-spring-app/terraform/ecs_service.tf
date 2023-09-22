resource "aws_ecs_service" "flb-frontend-service" {
  cluster                            = aws_ecs_cluster.flb-ecs-cluster.arn
  deployment_maximum_percent         = 200
  deployment_minimum_healthy_percent = 100
  desired_count                      = 1
  enable_ecs_managed_tags            = true
  enable_execute_command             = false
  health_check_grace_period_seconds  = 0
  name                               = "flb-frontend-service"
  scheduling_strategy                = "REPLICA"
  tags                               = {}
  tags_all                           = {}
  task_definition                    = aws_ecs_task_definition.flb-frontend.arn
  triggers                           = {}

  capacity_provider_strategy {
    base              = 0
    capacity_provider = "FARGATE"
    weight            = 1
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  load_balancer {
    container_name   = "flb-frontend"
    container_port   = 80
    target_group_arn = aws_lb_target_group.ecs-flb-frontend-tg.arn
  }

  network_configuration {
    assign_public_ip = true
    security_groups = [
      aws_security_group.ecs-flb-frontend-sg.id
    ]
    subnets = [
      aws_subnet.public_subnet[0].id,
      aws_subnet.public_subnet[1].id,
    ]
  }
}

resource "aws_ecs_service" "flb-backend-service" {
  cluster                            = aws_ecs_cluster.flb-ecs-cluster.arn
  deployment_maximum_percent         = 200
  deployment_minimum_healthy_percent = 100
  desired_count                      = 1
  enable_ecs_managed_tags            = true
  enable_execute_command             = false
  health_check_grace_period_seconds  = 0
  name                               = "flb-backend-service"
  scheduling_strategy                = "REPLICA"
  tags                               = {}
  tags_all                           = {}
  task_definition                    = aws_ecs_task_definition.flb-backend.arn
  triggers                           = {}

  capacity_provider_strategy {
    base              = 0
    capacity_provider = "FARGATE_SPOT"
    weight            = 1
  }

  deployment_circuit_breaker {
    enable   = true
    rollback = true
  }

  load_balancer {
    container_name   = "flb-backend"
    container_port   = 8000
    target_group_arn = aws_lb_target_group.ecs-flb-backend-tg.arn
  }

  network_configuration {
    assign_public_ip = false
    security_groups = [
      aws_security_group.ecs-flb-backend-sg.id,
    ]
    subnets = [
      aws_subnet.private_subnet[0].id,
      aws_subnet.private_subnet[1].id,
    ]
  }
}
