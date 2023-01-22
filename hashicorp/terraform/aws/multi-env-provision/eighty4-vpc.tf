resource "aws_vpc" "eighty4-vpc" {
  cidr_block = local.eighty4.vpc_cidr
  instance_tenancy = "default"

  tags = {
    Name = "eighty4-vpc"
  }
}

resource "aws_internet_gateway" "eighty4-ig" {
  vpc_id = aws_vpc.eighty4-vpc.id

  tags = {
    Name = "eighty4-ig"
  }
}
