resource "aws_ecs_task_definition" "flb-frontend" {
  container_definitions = jsonencode(
    [
      {
        cpu              = 0
        environment      = []
        environmentFiles = []
        essential        = true
        image            = format("%s.dkr.ecr.%s.amazonaws.com/flaschenbook-frontend:latest", data.aws_caller_identity.current.account_id, data.aws_region.current.name)
        logConfiguration = {
          logDriver = "awslogs"
          options = {
            awslogs-create-group  = "true"
            awslogs-group         = aws_cloudwatch_log_group.ecs-flb-frontend.name
            awslogs-region        = data.aws_region.current.name
            awslogs-stream-prefix = "ecs"
          }
          secretOptions = []
        }
        mountPoints = []
        name        = "flb-frontend"
        portMappings = [
          {
            appProtocol   = "http"
            containerPort = 80
            hostPort      = 80
            name          = "flb-frontend-80-tcp"
            protocol      = "tcp"
          },
        ]
        ulimits     = []
        volumesFrom = []
      },
    ]
  )
  cpu                = "512"
  execution_role_arn = aws_iam_role.ecs-task-execution-role.arn
  family             = "flb-frontend"
  memory             = "1024"
  network_mode       = "awsvpc"
  requires_compatibilities = [
    "FARGATE",
  ]
  tags          = {}
  tags_all      = {}
  task_role_arn = aws_iam_role.ecs-task-role.arn

  runtime_platform {
    cpu_architecture        = "X86_64"
    operating_system_family = "LINUX"
  }
}


resource "aws_ecs_task_definition" "flb-backend" {
  container_definitions = jsonencode(
    [
      {
        cpu = 0
        environment = [
          {
            name  = "DATABASE_PASSWORD"
            value = var.db_password
          },
          {
            name  = "DATABASE_URL"
            value = var.db_conn_url
          },
          {
            name  = "DATABASE_USERNAME"
            value = var.db_username
          },
        ]
        environmentFiles = []
        essential        = true
        image            = format("%s.dkr.ecr.%s.amazonaws.com/flaschenbook-backend:latest", data.aws_caller_identity.current.account_id, data.aws_region.current.name)
        logConfiguration = {
          logDriver = "awslogs"
          options = {
            awslogs-create-group  = "true"
            awslogs-group         = aws_cloudwatch_log_group.ecs-flb-backend.name
            awslogs-region        = data.aws_region.current.name
            awslogs-stream-prefix = "ecs"
          }
          secretOptions = []
        }
        mountPoints = []
        name        = "flb-backend"
        portMappings = [
          {
            appProtocol   = "http"
            containerPort = 8000
            hostPort      = 8000
            name          = "flb-backend-8000-tcp"
            protocol      = "tcp"
          },
        ]
        ulimits     = []
        volumesFrom = []
      },
    ]
  )
  cpu                = "512"
  execution_role_arn = aws_iam_role.ecs-task-execution-role.arn
  family             = "flb-backend"
  memory             = "1024"
  network_mode       = "awsvpc"
  requires_compatibilities = [
    "FARGATE",
  ]
  tags          = {}
  tags_all      = {}
  task_role_arn = aws_iam_role.ecs-task-role.arn

  runtime_platform {
    cpu_architecture        = "X86_64"
    operating_system_family = "LINUX"
  }
}

resource "aws_ecs_task_definition" "flb-prometheus" {
  family                   = "flb-prometheus"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "256"
  memory                   = "512"
  execution_role_arn       = aws_iam_role.ecs-task-execution-role.arn

  container_definitions = jsonencode([{
    name  = "flb-prometheus"
    image = format("%s.dkr.ecr.%s.amazonaws.com/flaschenbook-prometheus:latest", data.aws_caller_identity.current.account_id, data.aws_region.current.name)
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs-flb-prometheus.name,
        "awslogs-region"        = data.aws_region.current.name,
        "awslogs-stream-prefix" = "ecs"
      }
    }

    portMappings = [{
      containerPort = 9090
      hostPort      = 9090
    }]
  }])
}

resource "aws_ecs_task_definition" "flb-grafana" {
  family                   = "flb-grafana"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.ecs-task-execution-role.arn

  container_definitions = jsonencode([{
    name  = "flb-grafana"
    image = "grafana/grafana:latest"
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.ecs-flb-grafana.name,
        "awslogs-region"        = data.aws_region.current.name,
        "awslogs-stream-prefix" = "ecs"
      }
    }
    portMappings = [{
      containerPort = 3000
      hostPort      = 3000
    }],
    environment = [{
      name  = "GF_SECURITY_ADMIN_PASSWORD",
      value = var.grafana_password
      }, {
      name  = "GF_DATASOURCES_DEFAULT_URL",
      value = "http://${aws_lb.ecs-flb-prometheus-alb.dns_name}"
    }]
  }])
}
