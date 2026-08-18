# Local run via declarative Maven profile (-Plocal-run). No PATH surgery for JavaFX.
# Requires JDK 25 on PATH (or JAVA_HOME). Optional pins: mise.toml / .sdkmanrc
#
#   .\scripts\afx-run.ps1
#   ./scripts/afx-run.sh

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
if (-not (Test-Path (Join-Path $root 'pom.xml'))) {
    $root = (Get-Location).Path
}
Set-Location $root

if (Test-Path (Join-Path $root 'mvnw.cmd')) {
    $mvn = Join-Path $root 'mvnw.cmd'
} elseif (Get-Command mvn -ErrorAction SilentlyContinue) {
    $mvn = 'mvn'
} else {
    throw 'Maven not found. Install Maven, put it on PATH, or add Maven Wrapper (mvnw).'
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw 'Java not found. Install JDK 25 and set JAVA_HOME (see mise.toml / .sdkmanrc).'
}

Write-Host "Using: $mvn"
Write-Host "Compiling then running (kills stale target/classes)."
& $mvn -DskipTests -Plocal-run compile spring-boot:run @args
