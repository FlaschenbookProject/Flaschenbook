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
  cpu                = "1024"
  execution_role_arn = aws_iam_role.ecs-task-execution-role.arn
  family             = "flb-frontend"
  memory             = "3072"
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
  cpu                = "1024"
  execution_role_arn = aws_iam_role.ecs-task-execution-role.arn
  family             = "flb-backend"
  memory             = "3072"
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
