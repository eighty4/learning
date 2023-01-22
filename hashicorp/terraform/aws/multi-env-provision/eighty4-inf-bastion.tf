resource "aws_security_group" "eighty4-inf-bastion" {
  name = "eighty4-inf-bastion-sg"
  description = "Permit public inbound ssh and private network outbound ssh traffic"
  vpc_id = aws_vpc.eighty4-vpc.id

  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = [local.eighty4.vpc_cidr]
  }
}

resource "aws_instance" "eighty4-inf-bastion" {
  ami = "ami-037e4e289ed07850f"
  instance_type = "t2.nano"
  availability_zone = local.eighty4.availability_zone
  subnet_id = aws_subnet.eighty4-inf-public.id
  vpc_security_group_ids = [
    aws_security_group.eighty4-inf-bastion.id,
  ]

  tags = {
    Name = "eighty4-inf-bastion"
    Service = "bastion"
    Env = "inf"
  }
}
