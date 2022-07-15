resource "aws_subnet" "eighty4-dev-public" {
  vpc_id = aws_vpc.eighty4-vpc.id
  cidr_block = local.eighty4.apps.dev.public_subnet_cidr
  map_public_ip_on_launch = true
  availability_zone = local.eighty4.availability_zone

  tags = {
    Name = "eighty4-dev-public-subnet"
  }
}

resource "aws_route_table" "eighty4-dev-public" {
  vpc_id = aws_vpc.eighty4-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.eighty4-ig.id
  }

  tags = {
    Name = "eighty4-dev-public-route-table"
  }
}

resource "aws_route_table_association" "eighty4-dev-public" {
  subnet_id = aws_subnet.eighty4-dev-public.id
  route_table_id = aws_route_table.eighty4-dev-public.id
}

resource "aws_subnet" "eighty4-dev-private" {
  vpc_id = aws_vpc.eighty4-vpc.id
  cidr_block = local.eighty4.apps.dev.private_subnet_cidr
  map_public_ip_on_launch = false
  availability_zone = local.eighty4.availability_zone

  tags = {
    Name = "eighty4-dev-private-subnet"
  }
}

resource "aws_route_table" "eighty4-dev-private" {
  vpc_id = aws_vpc.eighty4-vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    instance_id = aws_instance.eighty4-inf-nat.id
  }

  tags = {
    Name = "eighty4-dev-private-route-table"
  }
}

resource "aws_route_table_association" "eighty4-dev-private" {
  subnet_id = aws_subnet.eighty4-dev-private.id
  route_table_id = aws_route_table.eighty4-dev-private.id
}
