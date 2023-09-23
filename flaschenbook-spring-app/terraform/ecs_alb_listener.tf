resource "aws_lb_listener" "ecs-flb-frontend-alb-listener" {
  load_balancer_arn = aws_lb.ecs-flb-frontend-alb.arn
  port              = 80
  protocol          = "HTTP"
  tags              = {}
  tags_all          = {}

  default_action {
    target_group_arn = aws_lb_target_group.ecs-flb-frontend-tg.arn
    type             = "forward"
  }
}

resource "aws_lb_listener" "ecs-flb-backend-alb-listener" {
  load_balancer_arn = aws_lb.ecs-flb-backend-alb.arn
  port              = 80
  protocol          = "HTTP"
  tags              = {}
  tags_all          = {}

  default_action {
    target_group_arn = aws_lb_target_group.ecs-flb-backend-tg.arn
    type             = "forward"
  }
}

resource "aws_lb_listener" "ecs-flb-prometheus-alb-listener" {
  load_balancer_arn = aws_lb.ecs-flb-prometheus-alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ecs-flb-prometheus-tg.arn
  }
}

resource "aws_lb_listener" "ecs-flb-grafana-alb-listener" {
  load_balancer_arn = aws_lb.ecs-flb-grafana-alb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.ecs-flb-grafana-tg.arn
  }
}

