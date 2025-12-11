variable "project" {
  type        = string
  description = "Project name"
}

variable "environment" {
  type        = string
  description = "environment (dev, prod, etc)"
}

variable "subnet_id" {
  type        = string
  description = "Subnet ID where EC2 instances will be launched"
}

variable "nginx_sg_id" {
  type        = string
  description = "Security group ID for nginx"
}

variable "was_sg_id" {
  type        = string
  description = "Security group ID for WAS"
}

variable "db_sg_id" {
  type        = string
  description = "Security group ID for DB"
}

variable "instance_type_nginx" {
  type        = string
  description = "Instance type for nginx"
}

variable "instance_type_was" {
  type        = string
  description = "Instance type for WAS"
}

variable "instance_type_db" {
  type        = string
  description = "Instance type for DB"
}

variable "key_name_was" {
  type = string
}

variable "key_name_db" {
  type = string
}

variable "region" {
  type        = string
  description = "AWS region (e.g., ap-northeast-2)"
}
