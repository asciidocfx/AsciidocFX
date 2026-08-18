#!/usr/bin/env bash
# Local run via Maven profile -Plocal-run. No PATH surgery for JavaFX.
#
# Maven lookup (first hit wins):
#   1. Repo wrapper: ./mvnw / ./mvnw.cmd (none ships today; still checked)
#   2. Project pins: mise which mvn when mise.toml exists and mise is installed;
#      sdkman maven candidate when .sdkmanrc exists
#   3. Workstation tools hive ($code / $CODE_ROOT / C:\code / Z:\code):
#      tools/apache-maven-*/bin/mvn — prefer 3.9.6, else newest
#   4. mvn on PATH last
#
# PATH-only failed on Windows: Maven is often installed off PATH, and a packaged
# JDK 24 (Adoptium, etc.) may be on PATH while this project wants JDK 25.
# Hive paths are optional — without them, wrapper / mise / sdkman / PATH still work.
#
# JDK: keep JAVA_HOME when it already looks like 25; otherwise mise, sdkman, or
# tools/jdk-25.
set -euo pipefail
root="$(cd "$(dirname "$0")/.." && pwd)"
cd "$root"

hive_roots=()
seen="|"
for h in "${code:-}" "${CODE_ROOT:-}" "/c/code" "/z/code" "C:/code" "Z:/code"; do
  [[ -n "$h" ]] || continue
  h="${h%/}"
  case "$seen" in
    *"|$h|"*) continue ;;
  esac
  seen="${seen}${h}|"
  hive_roots+=("$h")
done

java_home_looks_25() {
  local home="${1:-}"
  [[ -n "$home" ]] || return 1
  [[ -e "$home/bin/java" || -e "$home/bin/java.exe" ]] || return 1
  local leaf="${home##*/}"
  [[ "$leaf" == *25* ]]
}

sdkman_dir() {
  if [[ -n "${SDKMAN_DIR:-}" ]]; then
    echo "${SDKMAN_DIR%/}"
    return 0
  fi
  local home="${HOME:-}"
  [[ -n "$home" && -d "$home/.sdkman" ]] || return 1
  echo "$home/.sdkman"
}

sdkman_pin() {
  local tool="$1"
  [[ -f .sdkmanrc ]] || return 1
  local line
  line="$(grep -E "^[[:space:]]*${tool}=" .sdkmanrc | tail -n1 || true)"
  [[ -n "$line" ]] || return 1
  echo "${line#*=}"
}

try_mvn() {
  local p="${1:-}"
  [[ -n "$p" && -e "$p" ]] || return 1
  mvn_cmd=("$p")
}

mvn_cmd=()

if [[ -x ./mvnw ]]; then
  mvn_cmd=(./mvnw)
elif [[ -f ./mvnw.cmd ]]; then
  mvn_cmd=(./mvnw.cmd)
fi

if [[ ${#mvn_cmd[@]} -eq 0 && -f ./mise.toml ]] && command -v mise >/dev/null 2>&1; then
  which_mvn="$(mise which mvn 2>/dev/null || true)"
  which_mvn="$(printf '%s\n' "$which_mvn" | tail -n1)"
  try_mvn "$which_mvn" || true
fi

if [[ ${#mvn_cmd[@]} -eq 0 && -f .sdkmanrc ]]; then
  sdk="$(sdkman_dir || true)"
  if [[ -n "$sdk" ]]; then
    pin="$(sdkman_pin maven || true)"
    for n in $pin current; do
      [[ -n "$n" ]] || continue
      if try_mvn "$sdk/candidates/maven/$n/bin/mvn"; then
        break
      fi
      if try_mvn "$sdk/candidates/maven/$n/bin/mvn.cmd"; then
        break
      fi
    done
  fi
fi

if [[ ${#mvn_cmd[@]} -eq 0 ]]; then
  hive_paths=()
  hive_vers=()
  shopt -s nullglob
  for h in "${hive_roots[@]}"; do
    for dir in "$h"/tools/apache-maven-*; do
      [[ -d "$dir" ]] || continue
      cand=""
      if [[ -f "$dir/bin/mvn" ]]; then
        cand="$dir/bin/mvn"
      elif [[ -f "$dir/bin/mvn.cmd" ]]; then
        cand="$dir/bin/mvn.cmd"
      else
        continue
      fi
      hive_paths+=("$cand")
      hive_vers+=("${dir##*/apache-maven-}")
    done
  done
  shopt -u nullglob
  pick=""
  for i in "${!hive_vers[@]}"; do
    if [[ "${hive_vers[$i]}" == "3.9.6" ]]; then
      pick="${hive_paths[$i]}"
      break
    fi
  done
  if [[ -z "$pick" && ${#hive_paths[@]} -gt 0 ]]; then
    pick="${hive_paths[0]}"
    pick_ver="${hive_vers[0]}"
    for i in "${!hive_vers[@]}"; do
      v="${hive_vers[$i]}"
      newer="$(printf '%s\n%s\n' "$pick_ver" "$v" | sort -V | tail -n1)"
      if [[ "$newer" == "$v" && "$v" != "$pick_ver" ]]; then
        pick="${hive_paths[$i]}"
        pick_ver="$v"
      fi
    done
  fi
  if [[ -n "$pick" ]]; then
    mvn_cmd=("$pick")
  fi
fi

if [[ ${#mvn_cmd[@]} -eq 0 ]] && command -v mvn >/dev/null 2>&1; then
  mvn_cmd=("$(command -v mvn)")
fi

if [[ ${#mvn_cmd[@]} -eq 0 ]]; then
  echo "Maven not found. Install Maven, add a wrapper (mvnw), use mise/sdkman, put mvn on PATH, or install to tools/apache-maven-* under CODE_ROOT." >&2
  exit 1
fi

known_jdk25=""
if [[ -f ./mise.toml ]] && command -v mise >/dev/null 2>&1; then
  which_java="$(mise which java 2>/dev/null || true)"
  which_java="$(printf '%s\n' "$which_java" | tail -n1)"
  if [[ -n "$which_java" && -e "$which_java" ]]; then
    java_home_guess="$(cd "$(dirname "$which_java")/.." && pwd)"
    if java_home_looks_25 "$java_home_guess"; then
      known_jdk25="$java_home_guess"
    fi
  fi
fi
if [[ -z "$known_jdk25" && -f .sdkmanrc ]]; then
  sdk="$(sdkman_dir || true)"
  if [[ -n "$sdk" ]]; then
    pin="$(sdkman_pin java || true)"
    for n in $pin current; do
      [[ -n "$n" ]] || continue
      cand="$sdk/candidates/java/$n"
      if java_home_looks_25 "$cand"; then
        known_jdk25="$cand"
        break
      fi
    done
  fi
fi
if [[ -z "$known_jdk25" ]]; then
  for h in "${hive_roots[@]}"; do
    cand="$h/tools/jdk-25"
    if [[ -e "$cand/bin/java" || -e "$cand/bin/java.exe" ]]; then
      known_jdk25="$cand"
      break
    fi
  done
fi
if [[ -n "$known_jdk25" ]] && ! java_home_looks_25 "${JAVA_HOME:-}"; then
  export JAVA_HOME="$known_jdk25"
fi
if [[ -n "${JAVA_HOME:-}" && -d "$JAVA_HOME/bin" ]]; then
  export PATH="$JAVA_HOME/bin:$PATH"
fi

echo "Using: ${mvn_cmd[*]}"
if [[ -n "${JAVA_HOME:-}" ]]; then
  echo "JAVA_HOME: $JAVA_HOME"
fi
echo "JavaFX jars are copied by -Plocal-run from Maven Central (target/javafx-mods)."
exec "${mvn_cmd[@]}" -DskipTests -Plocal-run compile spring-boot:run "$@"
