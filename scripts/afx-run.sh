#!/usr/bin/env bash
# Local run — declarative Maven profile, no PATH surgery for JavaFX.
# Requires JDK 25. Optional: mise.toml / .sdkmanrc
set -euo pipefail
root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"
if [[ -x ./mvnw ]]; then
  mvn_cmd=(./mvnw)
elif command -v mvn >/dev/null 2>&1; then
  mvn_cmd=(mvn)
else
  echo "Maven not found (./mvnw or mvn on PATH)" >&2
  exit 1
fi
echo "JavaFX jars are copied by -Plocal-run from Maven Central (target/javafx-mods)."
exec "${mvn_cmd[@]}" -DskipTests -Plocal-run compile spring-boot:run "$@"
