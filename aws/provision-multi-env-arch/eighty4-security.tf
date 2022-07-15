resource "aws_security_group" "permit_inbound_http" {
  name = "permit_inbound_http"
  description = "Permit inbound http traffic"
  vpc_id = aws_vpc.eighty4-vpc.id

  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "permit_outbound_http" {
  name = "permit_outbound_http"
  description = "Permit outbound http traffic"
  vpc_id = aws_vpc.eighty4-vpc.id

  egress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "permit_icmp" {
  name = "permit_icmp"
  description = "Permit icmp traffic"
  vpc_id = aws_vpc.eighty4-vpc.id

  ingress {
    from_port = -1
    to_port = -1
    protocol = "icmp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = -1
    to_port = -1
    protocol = "icmp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "permit_internal_inbound_ssh" {
  name = "permit_ssh"
  description = "Permit internal inbound ssh traffic"
  vpc_id = aws_vpc.eighty4-vpc.id

  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = [
      local.eighty4.vpc_cidr,
    ]
  }
}

resource "aws_security_group" "permit_outbound_smtp" {
  name = "permit_smtp"
  description = "Permit outbound smtp traffic to Gandi"
  vpc_id = aws_vpc.eighty4-vpc.id

  egress {
    from_port = 587
    to_port = 587
    protocol = "tcp"
    cidr_blocks = [
      "217.70.178.9/32",
    ]
  }
}
