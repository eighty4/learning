locals {
  gcp_project = "eighty4-learning"
}

resource "google_service_account" "batch_acct" {
  project      = local.gcp_project
  account_id   = "batch-service-acct"
  display_name = "Batch Service Account"
}

data "google_iam_policy" "batch_auth" {
  binding {
    role    = "roles/run.invoker"
    members = [
      "serviceAccount:${google_service_account.batch_acct.email}",
    ]
  }
}

resource "google_cloud_run_service_iam_policy" "batch_auth" {
  location = google_cloud_run_service.default.location
  project  = google_cloud_run_service.default.project
  service  = google_cloud_run_service.default.name

  policy_data = data.google_iam_policy.batch_auth.policy_data
}

resource "google_project_service" "cloud_scheduler" {
  project            = local.gcp_project
  service            = "cloudscheduler.googleapis.com"
  disable_on_destroy = true
}

resource "google_project_service" "cloud_run" {
  project            = local.gcp_project
  service            = "run.googleapis.com"
  disable_on_destroy = true
}

resource "google_cloud_run_service" "default" {
  project  = local.gcp_project
  location = "us-central1"
  name     = "fomo-weekly-plot"

  template {
    spec {
      containers {
        image = "us-docker.pkg.dev/cloudrun/container/hello"
      }
    }
  }

  metadata {
    annotations = {

      #      "run.googleapis.com/ingress" = "internal"
      #      "run.googleapis.com/ingress" = "internal-and-cloud-load-balancing"
      #      "run.googleapis.com/ingress" = "all"
    }
  }

  traffic {
    percent         = 100
    latest_revision = true
  }

  depends_on = [
    google_project_service.cloud_run
  ]
}

resource "google_cloud_scheduler_job" "default" {
  project          = local.gcp_project
  region           = "us-central1"
  name             = "fomo-weekly-plot-schedule"
  description      = "test http job"
  schedule         = "* * * * *"
  time_zone        = "America/New_York"
  attempt_deadline = "320s"

  http_target {
    http_method = "GET"
    uri         = google_cloud_run_service.default.status[0].url

    oidc_token {
      service_account_email = google_service_account.batch_acct.email
    }
  }

  depends_on = [
    google_project_service.cloud_scheduler
  ]
}

