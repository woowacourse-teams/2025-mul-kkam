#!/usr/bin/env bash
set -euo pipefail

# CodeDeploy가 내려준 IMAGE_TAG 읽기
source /opt/mulkkam/env/image.env  # IMAGE_TAG=...

WORKDIR="/home/ubuntu/mulkkam"
COMPOSE_ENV="${WORKDIR}/.env"

# .env에 IMAGE_TAG 갱신(존재하면 교체, 없으면 추가)
mkdir -p "${WORKDIR}"
touch "${COMPOSE_ENV}"
if grep -q '^IMAGE_TAG=' "${COMPOSE_ENV}"; then
  sed -i "s/^IMAGE_TAG=.*/IMAGE_TAG=${IMAGE_TAG}/" "${COMPOSE_ENV}"
else
  echo "IMAGE_TAG=${IMAGE_TAG}" >> "${COMPOSE_ENV}"
fi

# 멱등 재기동
pushd "${WORKDIR}" >/dev/null
docker compose pull backend-app
docker compose up -d backend-app
popd >/dev/null

echo "[start] compose up done (IMAGE_TAG=${IMAGE_TAG})"
