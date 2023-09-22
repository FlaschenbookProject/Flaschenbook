resource "aws_codestarconnections_connection" "flb-github-connection" {
  provider_type = "GitHub"
  name          = "flb-github-connection"
}
