# provider
provider "aws" {
  region = var.aws_region
  profile = "${var.profile}"

}
