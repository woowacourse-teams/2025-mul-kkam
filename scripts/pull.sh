#!/usr/bin/env bash
set -euo pipefail

log(){ echo "[pull] $*"; }

# 이미지 태그 로드
source /opt/mulkkam/env/image.env   # IMAGE_TAG=...

IMAGE="mulkkam/mulkkam-prod:${IMAGE_TAG}"

log "pulling ${IMAGE}"
docker pull "${IMAGE}"
log "done pull"
