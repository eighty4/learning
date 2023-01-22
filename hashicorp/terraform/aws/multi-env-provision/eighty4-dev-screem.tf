resource "aws_lb" "eighty4-dev-screem-api-orchestrate" {
  name = "eighty4-dev-screem-api-orch-lb"
  load_balancer_type = "network"
  subnets = [
    aws_subnet.eighty4-inf-public.id
  ]

  idle_timeout = 400

  tags = {
    Name = "eighty4-dev-screem-api-orchestrate-lb"
  }
}

resource "aws_lb_listener" "eighty4-dev-screem-api-orchestrate" {
  load_balancer_arn = aws_lb.eighty4-dev-screem-api-orchestrate.arn
  port = "80"
  protocol = "TCP"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.eighty4-dev-screem-api-orchestrate.arn
  }
}

resource "aws_lb_target_group" "eighty4-dev-screem-api-orchestrate" {
  vpc_id = aws_vpc.eighty4-vpc.id
  port = "8080"
  protocol = "TCP"
  target_type = "instance"
  deregistration_delay = 90

  health_check {
    interval = 30
    port = "8080"
    protocol = "TCP"
    healthy_threshold = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_target_group_attachment" "eighty4-dev-screem-api-orchestrate" {
  target_group_arn = aws_lb_target_group.eighty4-dev-screem-api-orchestrate.arn
  target_id = aws_instance.eighty4-dev-screem-api.id
  port = "8080"
}

resource "aws_lb" "eighty4-dev-screem-api-proxy" {
  name = "eighty4-dev-screem-api-proxy-lb"
  load_balancer_type = "network"
  subnets = [
    aws_subnet.eighty4-inf-public.id
  ]

  idle_timeout = 400

  tags = {
    Name = "eighty4-dev-screem-api-proxy-lb"
  }
}

resource "aws_lb_listener" "eighty4-dev-screem-api-proxy" {
  load_balancer_arn = aws_lb.eighty4-dev-screem-api-proxy.arn
  port = "80"
  protocol = "TCP"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.eighty4-dev-screem-api-proxy.arn
  }
}

resource "aws_lb_target_group" "eighty4-dev-screem-api-proxy" {
  vpc_id = aws_vpc.eighty4-vpc.id
  port = "8081"
  protocol = "TCP"
  target_type = "instance"
  deregistration_delay = 90

  health_check {
    interval = 30
    port = "8081"
    protocol = "TCP"
    healthy_threshold = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_target_group_attachment" "eighty4-dev-screem-api-proxy" {
  target_group_arn = aws_lb_target_group.eighty4-dev-screem-api-proxy.arn
  target_id = aws_instance.eighty4-dev-screem-api.id
  port = "8081"
}

resource "aws_lb_listener" "eighty4-dev-screem-api-proxy-tcp" {
  load_balancer_arn = aws_lb.eighty4-dev-screem-api-proxy.arn
  port = "8082"
  protocol = "TCP"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.eighty4-dev-screem-api-proxy-tcp.arn
  }
}

resource "aws_lb_target_group" "eighty4-dev-screem-api-proxy-tcp" {
  vpc_id = aws_vpc.eighty4-vpc.id
  port = "8082"
  protocol = "TCP"
  target_type = "instance"
  deregistration_delay = 90

  health_check {
    interval = 30
    port = "8082"
    protocol = "TCP"
    healthy_threshold = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_target_group_attachment" "eighty4-dev-screem-api-proxy-tcp" {
  target_group_arn = aws_lb_target_group.eighty4-dev-screem-api-proxy-tcp.arn
  target_id = aws_instance.eighty4-dev-screem-api.id
  port = "8082"
}

resource "aws_security_group" "eighty4-dev-screem-api" {
  name = "screem-dev-api-sg"
  description = "Permit inbound tcp on 8080, 8081, 8082 and outbound to Cassandra"

  // orchestrate
  ingress {
    from_port = 8080
    to_port = 8080
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  // proxy http
  ingress {
    from_port = 8081
    to_port = 8081
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  // proxy tcp
  ingress {
    from_port = 8082
    to_port = 8082
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 9042
    to_port = 9042
    protocol = "tcp"
    cidr_blocks = ["10.0.220.164/32"]
  }

  vpc_id = aws_vpc.eighty4-vpc.id
}

resource "aws_instance" "eighty4-dev-screem-api" {
  ami = "ami-0914ab506e6384cb1"
  instance_type = "t2.small"
  availability_zone = local.eighty4.availability_zone
  subnet_id = aws_subnet.eighty4-dev-public.id
  vpc_security_group_ids = [
    aws_security_group.permit_icmp.id,
    aws_security_group.permit_internal_inbound_ssh.id,
    aws_security_group.permit_outbound_http.id,
    aws_security_group.permit_outbound_smtp.id,
    aws_security_group.eighty4-dev-screem-api.id,
  ]

  tags = {
    Name = "eighty4-dev-screem-api"
    Service = "screem-api"
    Env = "dev"
  }
}
