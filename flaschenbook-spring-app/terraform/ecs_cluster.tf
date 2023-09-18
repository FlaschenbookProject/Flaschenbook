resource "aws_ecs_cluster" "flb-ecs-cluster" {
  name = "flb-ecs-cluster"
  tags = {
    "Name" = "flb-ecs-cluster"
  }
  tags_all = {
    "Name" = "flb-ecs-cluster"
  }
}

resource "aws_ecs_cluster_capacity_providers" "flb-ecs-cluster-cp" {
  capacity_providers = [
    "FARGATE",
    "FARGATE_SPOT",
  ]
  cluster_name = aws_ecs_cluster.flb-ecs-cluster.name
}
