variable "project_id" {
  description = "The project ID"
  type        = string
  default     = "csye6225csye"
}

variable "zone" {
  description = "The zone"
  type        = string
  default     = "us-east1-b"
}

variable "artifact_path" {
  type    = string
  default = ""
}

variable "source_image_family" {
  type    = string
  default = "centos-stream-8"
}

variable "ssh_username" {
  type    = string
  default = "gcpImageUser"
}

variable "image_family" {
  type    = string
  default = "java-app-fam"
}

variable "image_name" {
  type    = string
  default = ""
}

locals {
  timestamp = regex_replace(timestamp(), "[- TZ:]", "")
}

source "googlecompute" "centos_stream" {
  project_id          = var.project_id
  zone                = var.zone
  machine_type        = "e2-medium"
  source_image_family = var.source_image_family
  ssh_username        = var.ssh_username
  image_name          = "gcp-${local.timestamp}"
  image_family        = var.image_family
}

build {
  sources = ["source.googlecompute.centos_stream"]
  provisioner "shell" {
    inline = [
      "sudo mkdir -p /opt",
      "sudo chown gcpImageUser:gcpImageUser /opt",
      "sudo mkdir -p /var/logs/cloud/",
      "sudo chown gcpImageUser:gcpImageUser /var/logs/cloud/"
    ]
  }

  provisioner "file" {
    destination = "/opt/"
    source      = "./../target/cloud-app-0.0.1-SNAPSHOT.jar"
  }

  provisioner "file" {
    destination = "/opt/"
    source      = "webservice.service"
  }

  provisioner "shell" {
    script = "script.sh"
  }

  provisioner "file" {
    destination = "/tmp/config.yaml"
    source      = "config.yaml"
  }

  provisioner "shell" {
    inline = [
      "sudo mv /tmp/config.yaml /etc/google-cloud-ops-agent/",
      "sudo chown root:root /etc/google-cloud-ops-agent/config.yaml"
    ]
  }
  /*provisioner "shell" {
    script = "mysql.sh"
  } */
}
