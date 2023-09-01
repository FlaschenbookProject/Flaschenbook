variable "aws_region" {
  default = "ap-southeast-1"
}

variable "availability_zones" {
  type    = list(string)
  default = ["ap-southeast-1a", "ap-southeast-1b", "ap-southeast-1c"]
}

variable "project_name" {
  default = "flaschenbookaf"
}

variable "s3_bucket" {
  default = "flaschenbook-airflow"
}

variable "fernet_key" {
  default = "fernet_key"
}

variable "profile" {
  default = "flaschenbook-profile"
}

variable "stage" {
  default = "dev"
}

variable "base_cidr_block" {
  default = "10.3.0.0"
}

variable "log_group_name" {
  default = "ecs/flaschenbook-af"
}

variable "image_version" {
  default = "latest"
}

variable "metadata_db_instance_type" {
  default = "db.t3.micro"
}

variable "celery_backend_instance_type" {
  default = "cache.t2.micro"
}
