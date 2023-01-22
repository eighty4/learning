locals {
  private_subnet_cidr_blocks = [
    local.eighty4.inf.private_subnet_cidr,
    local.eighty4.apps.prod.private_subnet_cidr,
    local.eighty4.apps.dev.private_subnet_cidr,
  ]
}

resource "aws_security_group" "eighty4-inf-nat" {
  name = "eighty4-inf-nat-sg"
  description = "Permit inbound http(s) traffic from private subnets"

  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = local.private_subnet_cidr_blocks
  }

  ingress {
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = local.private_subnet_cidr_blocks
  }

  vpc_id = aws_vpc.eighty4-vpc.id
}

resource "aws_instance" "eighty4-inf-nat" {
  ami = "ami-0422d936d535c63b1"
  availability_zone = local.eighty4.availability_zone
  instance_type = "t2.nano"
  vpc_security_group_ids = [
    aws_security_group.permit_icmp.id,
    aws_security_group.permit_internal_inbound_ssh.id,
    aws_security_group.permit_outbound_http.id,
    aws_security_group.eighty4-inf-nat.id,
  ]
  subnet_id = aws_subnet.eighty4-inf-public.id
  associate_public_ip_address = true
  source_dest_check = false

  tags = {
    Name = "eighty4-inf-nat"
  }
}
