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

variable "source_image_family" {
  type    = string
  default = "centos-stream-8"
}

variable "artifact_path" {
  type    = string
}

variable "ssh_username" {
  type    = string
  default = "gcpImageUser"
}

variable "image_family" {
  type    = string
  default = "java-app-fam"
}

locals {
  timestamp = regex_replace(timestamp(), "[- TZ:]", "")
}

source "googlecompute" "centos_stream" {
  project_id          = var.project_id
  zone                = var.zone
  machine_type        = "n1-standard-1"
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
            "sudo chown gcpImageUser:gcpImageUser /opt"
    ]
  }

  provisioner "file" {
    destination = "/opt/"
    source      = var.artifact_path
  }

  provisioner "file" {
    destination = "/opt/"
    source      = "webservice.service"
  }

  provisioner "shell" {
    script = "script.sh"
  }
  provisioner "shell" {
    script = "mysql.sh"
  }
}
