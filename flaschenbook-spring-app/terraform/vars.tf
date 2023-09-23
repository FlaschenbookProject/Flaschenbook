variable "aws_region" {}

variable "environment" {
  default = "flaschenbook"
}

variable "vpc_cidr" {
  default     = "10.0.0.0/16"
  description = "CIDR block of the vpc"
}

variable "public_subnets_cidr" {
  type        = list(any)
  default     = ["10.0.0.0/20", "10.0.128.0/20"]
  description = "CIDR block for Public Subnet"
}

variable "private_subnets_cidr" {
  type        = list(any)
  default     = ["10.0.16.0/20", "10.0.144.0/20"]
  description = "CIDR block for Private Subnet"
}
variable "codepipeline_bucket_name" {
  type    = string
  default = "flb-codepipeline-artifacts"
}
variable "repository_id" {
  type    = string
  default = "FlaschenbookProject/Flaschenbook"
}

variable "bucket_name" {}

variable "db_username" {}

variable "db_password" {}

variable "db_conn_url" {}

variable "key_name" {}

variable "root_directory" {}
