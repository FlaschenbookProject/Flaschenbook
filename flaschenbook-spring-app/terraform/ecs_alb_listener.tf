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

