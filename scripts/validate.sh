#!/usr/bin/env bash
set -euo pipefail

HEALTH_URL="http://localhost/actuator/health"

for i in {1..20}; do
  if curl -fsS "$HEALTH_URL" >/dev/null; then
    echo "[validate] OK"
    exit 0
  fi
  echo "[validate] retry $i"
  sleep 3
done

echo "[validate] FAILED"
exit 1
