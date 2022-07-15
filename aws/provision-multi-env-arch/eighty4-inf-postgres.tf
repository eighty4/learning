resource "aws_security_group" "eighty4-dev-postgres" {
  name = "eighty4-dev-postgres-sg"
  description = "Permit inbound tcp on 5432"
  vpc_id = aws_vpc.eighty4-vpc.id

  ingress {
    from_port = 5432
    to_port = 5432
    protocol = "tcp"
    cidr_blocks = [
      local.eighty4.inf.private_subnet_cidr,
    ]
  }
}

resource "aws_instance" "eighty4-dev-postgres" {
  ami = "ami-095e8d643118598ce"
  instance_type = "t2.nano"
  availability_zone = local.eighty4.availability_zone
  subnet_id = aws_subnet.eighty4-dev-private.id
  vpc_security_group_ids = [
    aws_security_group.permit_icmp.id,
    aws_security_group.permit_internal_inbound_ssh.id,
    aws_security_group.permit_outbound_http.id,
    aws_security_group.eighty4-dev-postgres.id,
  ]

  tags = {
    Name = "eighty4-dev-postgres"
    Service = "postgres"
    Env = "dev"
  }
}
