resource "aws_security_group" "ecs-flb-frontend-alb-sg" {
  name        = "ecs-flb-frontend-alb-sg"
  description = "flaschenbook service frontend application load balancer security group"

  # HTTP
  ingress {
    from_port = 80
    to_port   = 80
    protocol  = "tcp"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  # HTTPS
  ingress {
    from_port = 443
    to_port   = 443
    protocol  = "tcp"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  tags = {
    "Name" = "ecs-flb-frontend-alb-sg"
  }
  tags_all = {
    "Name" = "ecs-flb-frontend-alb-sg"
  }
  vpc_id = aws_vpc.vpc.id
}

resource "aws_security_group" "ecs-flb-frontend-sg" {
  name        = "ecs-flb-frontend-sg"
  description = "flaschenbook service frontend security group"
  ingress {
    from_port = 80
    to_port   = 80
    protocol  = "tcp"
    security_groups = [
      aws_security_group.ecs-flb-frontend-alb-sg.id,
    ]
  }
  ingress {
    from_port = 443
    to_port   = 443
    protocol  = "tcp"
    security_groups = [
      aws_security_group.ecs-flb-frontend-alb-sg.id,
    ]
  }
  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  tags = {
    "Name" = "ecs-flb-frontend-sg"
  }
  tags_all = {
    "Name" = "ecs-flb-frontend-sg"
  }
  vpc_id = aws_vpc.vpc.id
}

resource "aws_security_group" "ecs-flb-backend-alb-sg" {
  name        = "ecs-flb-backend-alb-sg"
  description = "flaschenbook backend application load balancer security group"
  ingress {
    from_port = 80
    to_port   = 80
    protocol  = "tcp"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  ingress {
    from_port = 443
    to_port   = 443
    protocol  = "tcp"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }

  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  tags = {
    "Name" = "ecs-flb-backend-alb-sg"
  }
  tags_all = {
    "Name" = "ecs-flb-backend-alb-sg"
  }
  vpc_id = aws_vpc.vpc.id
}

resource "aws_security_group" "ecs-flb-backend-sg" {
  name        = "ecs-flb-backend-sg"
  description = "flaschenbook backend security group"
  ingress {
    from_port = 8000
    to_port   = 8000
    protocol  = "tcp"
    security_groups = [
      aws_security_group.ecs-flb-backend-alb-sg.id,
    ]
  }
  ingress {
    from_port       = 9090
    to_port         = 9090
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs-flb-prometheus-sg.id]
  }
  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  tags = {
    "Name" = "ecs-flb-backend-sg"
  }
  tags_all = {
    "Name" = "ecs-flb-backend-sg"
  }
  vpc_id = aws_vpc.vpc.id
}

resource "aws_security_group" "ecs-flb-prometheus-alb-sg" {
  name        = "ecs-flb-prometheus-alb-sg"
  description = "Security group for Prometheus ALB"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port = 80
    to_port   = 80
    protocol  = "tcp"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  ingress {
    from_port = 443
    to_port   = 443
    protocol  = "tcp"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  ingress {
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecs-flb-prometheus-sg" {
  name        = "ecs-flb-prometheus-sg"
  description = "Security group for Prometheus service"
  vpc_id      = aws_vpc.vpc.id
  ingress {
    from_port = 9090
    to_port   = 9090
    protocol  = "tcp"
    security_groups = [
      aws_security_group.ecs-flb-prometheus-alb-sg.id,
      aws_security_group.ecs-flb-grafana-sg.id
    ]
  }
  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
}


resource "aws_security_group" "ecs-flb-grafana-alb-sg" {
  name        = "ecs-flb-grafana-alb-sg"
  description = "Security group for Grafana ALB"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port = 80
    to_port   = 80
    protocol  = "tcp"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  ingress {
    from_port = 443
    to_port   = 443
    protocol  = "tcp"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "ecs-flb-grafana-sg" {
  name        = "ecs-flb-grafana-sg"
  description = "Security group for Grafana service"
  vpc_id      = aws_vpc.vpc.id
  ingress {
    from_port = 3000
    to_port   = 3000
    protocol  = "tcp"
    security_groups = [
      aws_security_group.ecs-flb-grafana-alb-sg.id,
    ]
  }
  egress {
    from_port = 0
    to_port   = 0
    protocol  = "-1"
    cidr_blocks = [
      "0.0.0.0/0",
    ]
  }
}
