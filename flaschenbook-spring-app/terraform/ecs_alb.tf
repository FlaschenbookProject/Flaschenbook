resource "aws_lb" "ecs-flb-frontend-alb" {
  name                                        = "ecs-flb-frontend-alb"
  desync_mitigation_mode                      = "defensive"
  drop_invalid_header_fields                  = false
  enable_cross_zone_load_balancing            = true
  enable_deletion_protection                  = false
  enable_http2                                = true
  enable_tls_version_and_cipher_suite_headers = false
  enable_waf_fail_open                        = false
  enable_xff_client_port                      = false
  idle_timeout                                = 60
  internal                                    = false
  ip_address_type                             = "ipv4"
  load_balancer_type                          = "application"
  preserve_host_header                        = false
  security_groups = [
    aws_security_group.ecs-flb-frontend-alb-sg.id
  ]
  subnets = [
    aws_subnet.public_subnet[0].id,
    aws_subnet.public_subnet[1].id,
  ]
  tags                       = {}
  tags_all                   = {}
  xff_header_processing_mode = "append"

  access_logs {
    bucket  = ""
    enabled = false
  }
}

resource "aws_lb" "ecs-flb-backend-alb" {
  name                                        = "ecs-flb-backend-alb"
  desync_mitigation_mode                      = "defensive"
  drop_invalid_header_fields                  = false
  enable_cross_zone_load_balancing            = true
  enable_deletion_protection                  = false
  enable_http2                                = true
  enable_tls_version_and_cipher_suite_headers = false
  enable_waf_fail_open                        = false
  enable_xff_client_port                      = false
  idle_timeout                                = 60
  internal                                    = false
  ip_address_type                             = "ipv4"
  load_balancer_type                          = "application"
  preserve_host_header                        = false
  security_groups = [
    aws_security_group.ecs-flb-backend-alb-sg.id
  ]
  subnets = [
    aws_subnet.public_subnet[0].id,
    aws_subnet.public_subnet[1].id,
  ]
  tags                       = {}
  tags_all                   = {}
  xff_header_processing_mode = "append"

  access_logs {
    bucket  = ""
    enabled = false
  }

}

resource "aws_lb" "ecs-flb-prometheus-alb" {
  name                             = "ecs-flb-prometheus-alb"
  internal                         = false # temp
  load_balancer_type               = "application"
  security_groups                  = [aws_security_group.ecs-flb-prometheus-alb-sg.id]
  enable_deletion_protection       = false
  enable_cross_zone_load_balancing = true
  idle_timeout                     = 400
  enable_http2                     = true
  subnets = [
    aws_subnet.public_subnet[0].id,
    aws_subnet.public_subnet[1].id,
  ]
}

resource "aws_lb" "ecs-flb-grafana-alb" {
  name                             = "ecs-flb-grafana-alb"
  internal                         = false
  load_balancer_type               = "application"
  security_groups                  = [aws_security_group.ecs-flb-grafana-alb-sg.id]
  enable_deletion_protection       = false
  enable_cross_zone_load_balancing = true
  idle_timeout                     = 400
  enable_http2                     = true
  subnets = [
    aws_subnet.public_subnet[0].id,
    aws_subnet.public_subnet[1].id,
  ]
}
