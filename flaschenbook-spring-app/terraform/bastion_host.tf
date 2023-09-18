data "external" "current_ip" {
  program = ["bash", "-c", "echo '{\"ip\": \"'$(curl -s http://ifconfig.me/ip)'\"}'"]
}

resource "aws_security_group" "bastion_host_sg" {
  name        = "bastion_host_sg"
  description = "Security group for Bastion Host"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port   = 22 # SSH
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["${data.external.current_ip.result["ip"]}/32"]
  }

  # MySQL에 대한 접근을 허용 (옵션)
  ingress {
    from_port = 3306
    to_port   = 3306
    protocol  = "tcp"
    cidr_blocks = ["${aws_subnet.private_subnet[0].cidr_block}",
    "${aws_subnet.private_subnet[1].cidr_block}"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "bastion_host_sg"
  }
}

resource "aws_instance" "db_bastion_host" {
  ami                                  = "ami-091aca13f89c7964e"
  associate_public_ip_address          = true
  availability_zone                    = element(local.availability_zones, 0)
  disable_api_stop                     = false
  disable_api_termination              = false
  ebs_optimized                        = false
  get_password_data                    = false
  hibernation                          = false
  instance_initiated_shutdown_behavior = "stop"
  instance_type                        = "t2.micro"
  key_name                             = var.key_name
  monitoring                           = false
  placement_partition_number           = 0
  secondary_private_ips                = []
  security_groups                      = []
  source_dest_check                    = true
  subnet_id                            = aws_subnet.public_subnet[0].id
  tags = {
    "Name" = "db_bastion_host"
  }
  tags_all = {
    "Name" = "db_bastion_host"
  }
  tenancy = "default"
  vpc_security_group_ids = [
    aws_security_group.bastion_host_sg.id
  ]

  capacity_reservation_specification {
    capacity_reservation_preference = "open"
  }

  credit_specification {
    cpu_credits = "standard"
  }

  enclave_options {
    enabled = false
  }

  maintenance_options {
    auto_recovery = "default"
  }

  metadata_options {
    http_endpoint               = "enabled"
    http_put_response_hop_limit = 2
    http_tokens                 = "required"
    instance_metadata_tags      = "disabled"
  }

  private_dns_name_options {
    enable_resource_name_dns_a_record    = false
    enable_resource_name_dns_aaaa_record = false
    hostname_type                        = "ip-name"
  }

  root_block_device {
    delete_on_termination = true
    encrypted             = false
    iops                  = 3000
    tags                  = {}
    throughput            = 125
    volume_size           = 8
    volume_type           = "gp3"
  }
  user_data_replace_on_change = false
}
