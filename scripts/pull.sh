#!/usr/bin/env bash
set -euo pipefail

log(){ echo "[pull] $*"; }

# awscli v2 설치 (noble엔 apt awscli 없음)
if ! command -v aws >/dev/null 2>&1; then
  log "awscli v2 not found. installing..."
  apt-get update -y
  apt-get install -y unzip curl
  ARCH="$(uname -m)"
  case "$ARCH" in
    x86_64) URL="https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" ;;
    aarch64|arm64) URL="https://awscli.amazonaws.com/awscli-exe-linux-aarch64.zip" ;;
    *) log "unsupported arch ${ARCH}"; exit 1 ;;
  esac
  TMPDIR="$(mktemp -d)"; trap 'rm -rf "$TMPDIR"' EXIT
  curl -fsSL "$URL" -o "$TMPDIR/awscliv2.zip"
  unzip -q "$TMPDIR/awscliv2.zip" -d "$TMPDIR"
  sudo "$TMPDIR/aws/install" --update
  log "aws installed: $(aws --version)"
else
  log "aws exists: $(aws --version)"
fi

# 이미지 태그 로드
source /opt/mulkkam/env/image.env   # IMAGE_TAG=...

IMAGE="mulkkam/mulkkam-prod:${IMAGE_TAG}"

log "pulling ${IMAGE}"
docker pull "${IMAGE}"
log "done pull"
