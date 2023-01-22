provider "aws" {
  profile = "default"
  region = "us-east-1"
}

locals {
  eighty4 = {
    vpc_cidr = "10.0.0.0/16"
    availability_zone = "us-east-1c"
    apps = {
      prod = {
        public_subnet_cidr = "10.0.10.0/24"
        private_subnet_cidr = "10.0.20.0/24"
      }
      dev = {
        public_subnet_cidr = "10.0.210.0/24"
        private_subnet_cidr = "10.0.220.0/24"
      }
    }
    inf = {
      public_subnet_cidr = "10.0.2.0/24"
      private_subnet_cidr = "10.0.3.0/24"
      teamcity_port = "8111"
    }
  }
}
