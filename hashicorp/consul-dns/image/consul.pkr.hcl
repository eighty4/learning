locals {
  gcp_project_id = "eighty4-learning"
}

source "googlecompute" "consul_archetype" {
  project_id              = local.gcp_project_id
  image_name              = "eighty4-consul-${ formatdate("YYYY-MM-DD", timestamp()) }"
  image_family            = "eighty4-consul"
  source_image_family     = "debian-11"
  ssh_username            = "packer"
  zone                    = "us-central1-c"
  machine_type            = "e2-medium"
  pause_before_connecting = "30s"
}

build {
  sources = ["source.googlecompute.consul_archetype"]

  provisioner "ansible" {
    playbook_file = "./playbook.yml"
    user          = "packer"
  }

}
