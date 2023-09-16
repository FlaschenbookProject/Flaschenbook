# parameter group
resource "aws_db_parameter_group" "mysql-parameters" {
  name        = "mysql-parameters"
  family      = "mysql8.0"
  description = "mysql db parameter group"

}

resource "aws_db_subnet_group" "mysql-subnet" {
  name        = "mysql-subnet"
  description = "mysql db subnet group"
  subnet_ids  = ["${aws_subnet.private_subnet[0].id}", "${aws_subnet.private_subnet[1].id}"]
}

resource "aws_security_group" "mysql-sg" {
  vpc_id      = aws_vpc.vpc.id
  name        = "mysql-sg"
  description = "mysql security group"
  ingress {
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = ["${aws_security_group.glue_sg.id}"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  tags = {
    Name = "mysql security group"
  }
}

resource "aws_db_instance" "flaschenbook-db" {
  allocated_storage       = 100
  engine                  = "mysql"
  engine_version          = "8.0.33"
  instance_class          = "db.t2.micro"
  identifier              = "mysqldb"
  db_name                 = "dev"
  username                = var.db_username
  password                = var.db_password
  db_subnet_group_name    = aws_db_subnet_group.mysql-subnet.name
  parameter_group_name    = aws_db_parameter_group.mysql-parameters.name
  multi_az                = "false"
  vpc_security_group_ids  = ["${aws_security_group.mysql-sg.id}"]
  storage_type            = "gp2"
  backup_retention_period = 30
  availability_zone       = aws_subnet.private_subnet[0].availability_zone
  tags = {
    Name = "mysqldb-instance"
  }
}
