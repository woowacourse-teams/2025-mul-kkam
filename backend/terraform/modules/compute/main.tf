# 단일 환경만 생성

data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

resource "aws_iam_role" "ec2_ssm_role" {
  name = "${var.project}-ec2-ssm-role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [{
      Effect = "Allow",
      Principal = { Service = "ec2.amazonaws.com" },
      Action   = "sts:AssumeRole"
    }]
  })
}

resource "aws_iam_role_policy_attachment" "ec2_ssm_core" {
  role       = aws_iam_role.ec2_ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
}

resource "aws_iam_instance_profile" "ec2_ssm_profile" {
  name = "${var.project}-ec2-ssm-profile"
  role = aws_iam_role.ec2_ssm_role.name
}

resource "aws_instance" "nginx" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type_nginx
  subnet_id              = var.subnet_id
  vpc_security_group_ids = [var.nginx_sg_id]
  iam_instance_profile = aws_iam_instance_profile.ec2_ssm_profile.name

  associate_public_ip_address = true
  user_data                   = local.common_user_data

  tags = {
    Name = "${var.project}-${var.environment}-nginx"
    Env  = var.environment
  }
}

resource "aws_instance" "was" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type_was
  subnet_id              = var.subnet_id
  vpc_security_group_ids = [var.was_sg_id]
  key_name               = var.key_name_was
  iam_instance_profile = aws_iam_instance_profile.ec2_ssm_profile.name

  associate_public_ip_address = true
  user_data                   = local.common_user_data

  tags = {
    Name = "${var.project}-${var.environment}-was"
    Env  = var.environment
  }
}

resource "aws_instance" "db" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = var.instance_type_db
  subnet_id              = var.subnet_id
  vpc_security_group_ids = [var.db_sg_id]
  key_name               = var.key_name_db
  iam_instance_profile = aws_iam_instance_profile.ec2_ssm_profile.name
  associate_public_ip_address = true

  user_data                   = local.common_user_data

  tags = {
    Name = "${var.project}-${var.environment}-db"
    Env  = var.environment
  }
}

resource "aws_iam_role_policy_attachment" "ec2_cloudwatch" {
  role       = aws_iam_role.ec2_ssm_role.name
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchAgentServerPolicy"
}

resource "aws_eip" "nginx_eip" {
  instance = aws_instance.nginx.id
  domain   = "vpc"

  tags = {
    Name = "${var.project}-${var.environment}-nginx-eip"
    Env  = var.environment
  }
}

locals {
  common_user_data = <<EOF
#!/bin/bash
set -e

apt-get update -y
apt-get install -y docker.io

systemctl enable docker
systemctl start docker

snap install amazon-ssm-agent --classic
systemctl enable snap.amazon-ssm-agent.amazon-ssm-agent.service
systemctl start snap.amazon-ssm-agent.amazon-ssm-agent.service
EOF
}
