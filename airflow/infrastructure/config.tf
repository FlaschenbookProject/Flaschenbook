variable "aws_region" {
   default = "ap-northeast-2"
}

variable "availability_zones" {
   type    = list(string)
   default = ["ap-northeast-2a", "ap-northeast-2b", "ap-northeast-2c"]
}

variable "project_name" {
   default = "flaschenbookaf"
}

variable "s3_bucket" {
   default="de-4-1-airflow"
}

variable "fernet_key"{
   default= "H8JoQIyBvIvef1V_tootxuU1K_4iQ2PNsb4RV33jX_0="
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
   default = "ecs/flaschenbookaf"
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