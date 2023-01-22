locals {
  gcp_project_id = "eighty4-learning"
}

provider google {
  project = local.gcp_project_id
}

data "google_compute_network" "network" {
  name = "default"
}

resource "google_service_account" "consul" {
  account_id   = "consul-service-acct"
  display_name = "Consul Cluster Service Account"
}

resource "google_project_iam_binding" "consul" {
  project = local.gcp_project_id
  role    = "roles/compute.networkViewer"
  members = [
    "serviceAccount:${google_service_account.consul.email}"
  ]
}

resource "google_compute_instance_template" "consul" {
  name_prefix  = "consul-server-template-"
  machine_type = "e2-medium"
  region       = "us-central1"
  tags         = ["consul"]

  disk {
    source_image = "${local.gcp_project_id}/eighty4-consul"
  }

  network_interface {
    access_config {}
    network = data.google_compute_network.network.self_link
  }

  lifecycle {
    create_before_destroy = true
  }

  service_account {
    email  = google_service_account.consul.email
    scopes = ["cloud-platform"]
  }
}

resource "google_compute_region_instance_group_manager" "consul" {
  name                      = "consul-cluster"
  base_instance_name        = "consul-server"
  region                    = "us-central1"
  distribution_policy_zones = ["us-central1-a", "us-central1-b", "us-central1-f"]
  target_size               = "3"
  version {
    instance_template = google_compute_instance_template.consul.id
  }
}
