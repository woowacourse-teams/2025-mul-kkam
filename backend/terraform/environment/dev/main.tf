module "network" {
  source = "../../modules/network"

  project              = var.project
  region               = var.region
  environment          = var.environment
  vpc_cidr             = var.vpc_cidr
  public_subnet_cidr   = var.public_subnet_cidr
}

module "compute" {
  source = "../../modules/compute"

  project             = var.project
  region              = var.region
  environment         = var.environment
  subnet_id           = module.network.subnet_id
  nginx_sg_id         = module.network.nginx_sg_id
  was_sg_id           = module.network.was_sg_id
  db_sg_id            = module.network.db_sg_id
  instance_type_nginx = var.instance_type_nginx
  instance_type_was   = var.instance_type_was
  instance_type_db    = var.instance_type_db
  key_name_was        = var.key_name_was
  key_name_db         = var.key_name_db
}
