# db connection
resource "aws_security_group" "glue_sg" {
  name        = "GlueJDBCConnectionSG"
  description = "Security Group for AWS Glue to access RDS"
  vpc_id      = aws_vpc.vpc.id

  ingress {
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["${aws_subnet.private_subnet[0].cidr_block}"]
  }

  ingress {
    from_port   = 0
    to_port     = 65535
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

resource "aws_glue_connection" "flb-db-conn" {
  connection_properties = {
    JDBC_ENFORCE_SSL    = "false"
    JDBC_CONNECTION_URL = var.db_conn_url
    PASSWORD            = var.db_password
    USERNAME            = var.db_username
    KAFKA_SSL_ENABLED   = "false"
  }
  connection_type = "JDBC"
  name            = "flb-db-conn"

  physical_connection_requirements {
    availability_zone = aws_subnet.private_subnet[0].availability_zone
    security_group_id_list = [
      aws_security_group.glue_sg.id,
    ]
    subnet_id = aws_subnet.private_subnet[0].id
  }
}
