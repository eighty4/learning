resource "aws_subnet" "eighty4-inf-public" {
  vpc_id = aws_vpc.eighty4-vpc.id
  cidr_block = local.eighty4.inf.public_subnet_cidr
  map_public_ip_on_launch = true
  availability_zone = local.eighty4.availability_zone

  tags = {
    Name = "eighty4-inf-public-subnet"
  }
}

resource "aws_route_table" "eighty4-inf-public" {
  vpc_id = aws_vpc.eighty4-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.eighty4-ig.id
  }

  tags = {
    Name = "eighty4-inf-public-route-table"
  }
}

resource "aws_route_table_association" "eighty4-inf-public" {
  subnet_id = aws_subnet.eighty4-inf-public.id
  route_table_id = aws_route_table.eighty4-inf-public.id
}

resource "aws_subnet" "eighty4-inf-private" {
  vpc_id = aws_vpc.eighty4-vpc.id
  cidr_block = local.eighty4.inf.private_subnet_cidr
  map_public_ip_on_launch = false
  availability_zone = local.eighty4.availability_zone

  tags = {
    Name = "eighty4-inf-private-subnet"
  }
}

resource "aws_route_table" "eighty4-inf-private" {
  vpc_id = aws_vpc.eighty4-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    instance_id = aws_instance.eighty4-inf-nat.id
  }

  tags = {
    Name = "eighty4-inf-private-route-table"
  }
}

resource "aws_route_table_association" "eighty4-inf-private" {
  subnet_id = aws_subnet.eighty4-inf-private.id
  route_table_id = aws_route_table.eighty4-inf-private.id
}
