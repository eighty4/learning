resource "aws_lb" "eighty4-dev-cassandra" {
  name = "eighty4-dev-cassandra-lb"
  load_balancer_type = "network"
  subnets = [
    aws_subnet.eighty4-inf-private.id
  ]

  idle_timeout = 400

  tags = {
    Name = "eighty4-dev-cassandra-lb"
  }
}

resource "aws_lb_listener" "eighty4-dev-cassandra" {
  load_balancer_arn = aws_lb.eighty4-dev-cassandra.arn
  port = "9042"
  protocol = "TCP"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.eighty4-dev-cassandra.arn
  }
}

resource "aws_lb_target_group" "eighty4-dev-cassandra" {
  vpc_id = aws_vpc.eighty4-vpc.id
  port = "9042"
  protocol = "TCP"
  target_type = "instance"
  deregistration_delay = 90

  health_check {
    interval = 30
    port = "9042"
    protocol = "TCP"
    healthy_threshold = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_target_group_attachment" "eighty4-dev-cassandra" {
  target_group_arn = aws_lb_target_group.eighty4-dev-cassandra.arn
  target_id = aws_instance.eighty4-dev-cassandra.id
  port = "9042"
}

resource "aws_security_group" "eighty4-dev-cassandra" {
  name = "eighty4-dev-cassandra-sg"
  description = "Permit inbound tcp on 9042"
  vpc_id = aws_vpc.eighty4-vpc.id

  ingress {
    from_port = 9042
    to_port = 9042
    protocol = "tcp"
    cidr_blocks = [
      local.eighty4.apps.dev.public_subnet_cidr,
      local.eighty4.apps.dev.private_subnet_cidr,
    ]
  }
}

resource "aws_instance" "eighty4-dev-cassandra" {
  ami = "ami-05b0e723712e1844c"
  instance_type = "t2.small"
  availability_zone = local.eighty4.availability_zone
  subnet_id = aws_subnet.eighty4-dev-private.id
  vpc_security_group_ids = [
    aws_security_group.permit_icmp.id,
    aws_security_group.permit_internal_inbound_ssh.id,
    aws_security_group.permit_outbound_http.id,
    aws_security_group.eighty4-dev-cassandra.id,
  ]

  tags = {
    Name = "eighty4-dev-cassandra"
    Service = "cassandra"
    Env = "dev"
  }
}
