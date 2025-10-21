#!/usr/bin/env bash
set -euo pipefail

log(){ echo "[pull] $*"; }

# 1) 후보 경로 설정
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"   # deployment-archive 루트
CANDIDATES=(
  "/opt/mulkkam/env/image.env"
  "$ROOT_DIR/env/image.env"
)

# 2) 실제 존재하는 image.env 찾기
IMAGE_ENV=""
for p in "${CANDIDATES[@]}"; do
  if [[ -f "$p" ]]; then
    IMAGE_ENV="$p"
    break
  fi
done

if [[ -z "${IMAGE_ENV}" ]]; then
  echo "[pull] image.env not found in any of: ${CANDIDATES[*]}" >&2
  exit 1
fi
log "using image.env: ${IMAGE_ENV}"

# 3) key=value 로드
set -a
# shellcheck disable=SC1090
source "${IMAGE_ENV}"
set +a

: "${IMAGE_TAG:?IMAGE_TAG is required in image.env}"
IMAGE="mulkkam/mulkkam-prod:${IMAGE_TAG}"

# 4) 이미지 pull
log "pulling ${IMAGE}"
docker pull "${IMAGE}"
log "done pull"
