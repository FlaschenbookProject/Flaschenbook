resource "aws_ecr_repository" "flaschenbook-frontend" {
  name = "flaschenbook-frontend"
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "flaschenbook-backend" {
  name = "flaschenbook-backend"
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "flaschenbook-prometheus" {
  name = "flaschenbook-prometheus"
  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_repository" "node_14_alpine" {
  name                 = "node-14-alpine"
  image_tag_mutability = "MUTABLE"
  image_scanning_configuration {
    scan_on_push = true
  }
}
