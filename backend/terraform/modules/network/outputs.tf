output "vpc_id" {
  value = aws_vpc.main.id
}

output "subnet_id" {
  value = aws_subnet.public.id
}

output "nginx_sg_id" {
  value = aws_security_group.nginx_sg.id
}

output "was_sg_id" {
  value = aws_security_group.was_sg.id
}

output "db_sg_id" {
  value = aws_security_group.db_sg.id
}
