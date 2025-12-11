output "nginx_instances" {
  value = aws_instance.nginx
}

output "was_instances" {
  value = aws_instance.was
}

output "db_instances" {
  value = aws_instance.db
}

output "nginx_eips" {
  value = aws_eip.nginx_eip.public_ip
}

output "nginx_public_ip" {
  value = aws_instance.nginx.public_ip
}

output "was_private_ip" {
  value = aws_instance.was.private_ip
}

output "db_private_ip" {
  value = aws_instance.db.private_ip
}
