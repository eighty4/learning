resource "aws_lb" "eighty4-inf-teamcity-lb" {
  name = "eighty4-teamcity-lb"
  load_balancer_type = "network"
  subnets = [
    aws_subnet.eighty4-inf-public.id
  ]

  idle_timeout = 400

  tags = {
    Name = "eighty4-teamcity-lb"
  }
}

resource "aws_lb_listener" "eighty4-inf-teamcity-lb" {
  load_balancer_arn = aws_lb.eighty4-inf-teamcity-lb.arn
  port = "80"
  protocol = "TCP"

  default_action {
    type = "forward"
    target_group_arn = aws_lb_target_group.eighty4-inf-teamcity-lb.arn
  }
}

resource "aws_lb_target_group" "eighty4-inf-teamcity-lb" {
  vpc_id = aws_vpc.eighty4-vpc.id
  port = local.eighty4.inf.teamcity_port
  protocol = "TCP"
  target_type = "instance"
  deregistration_delay = 90

  health_check {
    interval = 30
    port = local.eighty4.inf.teamcity_port
    protocol = "TCP"
    healthy_threshold = 3
    unhealthy_threshold = 3
  }
}

resource "aws_lb_target_group_attachment" "eighty4-inf-teamcity-lb" {
  target_group_arn = aws_lb_target_group.eighty4-inf-teamcity-lb.arn
  target_id = aws_instance.eighty4-inf-teamcity.id
  port = local.eighty4.inf.teamcity_port
}

resource "aws_security_group" "eighty4-inf-teamcity" {
  name = "eighty4-inf-teamcity-sg"
  description = "Permit inbound on 8111"

  ingress {
    from_port = 8111
    to_port = 8111
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 5432
    to_port = 5432
    protocol = "tcp"
    cidr_blocks = ["10.0.220.59/32"]
  }

  vpc_id = aws_vpc.eighty4-vpc.id
}

resource "aws_instance" "eighty4-inf-teamcity" {
  ami = "ami-01956d9455b89ff60"
  instance_type = "t2.small"
  availability_zone = local.eighty4.availability_zone
  subnet_id = aws_subnet.eighty4-inf-private.id
  vpc_security_group_ids = [
    aws_security_group.permit_icmp.id,
    aws_security_group.permit_internal_inbound_ssh.id,
    aws_security_group.permit_outbound_http.id,
    aws_security_group.eighty4-inf-teamcity.id,
  ]

  tags = {
    Name = "eighty4-inf-teamcity"
    Service = "teamcity"
    Env = "inf"
  }
}

resource "aws_instance" "eighty4-inf-teamcity-agent" {
  ami = "ami-0914ab506e6384cb1"
  instance_type = "t2.micro"
  availability_zone = local.eighty4.availability_zone
  subnet_id = aws_subnet.eighty4-inf-private.id
  vpc_security_group_ids = [
    aws_security_group.permit_icmp.id,
    aws_security_group.permit_internal_inbound_ssh.id,
    aws_security_group.permit_outbound_http.id,
  ]
  user_data = <<-EOT
    echo "SERVER_URL=${aws_lb.eighty4-inf-teamcity-lb.dns_name}" >> /etc/environment
    echo "AGENT_NAME=agent-${count.index}" >> /etc/environment
  EOT

  tags = {
    Name = "eighty4-inf-teamcity-agent"
    Service = "teamcity-agent"
    Env = "inf"
  }
}
