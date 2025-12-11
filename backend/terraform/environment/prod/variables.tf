variable "region" {
  type    = string
  default = "ap-northeast-2"
}

variable "project" {
  type    = string
  default = "mulkkam"
}

variable "environment" {
  type    = string
  default = "prod"
}

variable "vpc_cidr" {
  type    = string
  default = "10.0.0.0/16"
}

variable "public_subnet_cidr" {
  type    = string
  default = "10.0.1.0/24"
}

variable "instance_type_nginx" {
  type    = string
  default = "t3.micro"
}

variable "instance_type_was" {
  type    = string
  default = "t3.micro"
}

variable "instance_type_db" {
  type    = string
  default = "t3.micro"
}

variable "key_name" {
  type    = string
  default = "mulkkam-prod-~~(tmp)"
}
