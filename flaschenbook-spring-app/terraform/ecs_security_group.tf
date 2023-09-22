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
