# Local run via Maven profile -Plocal-run. No PATH surgery for JavaFX.
#
# Maven lookup (first hit wins):
#   1. Repo wrapper: mvnw.cmd / mvnw (none ships today; still checked)
#   2. Project pins: mise which mvn when mise.toml exists and mise is installed;
#      sdkman maven candidate when .sdkmanrc exists
#   3. Workstation tools hive (%code% / %CODE_ROOT% / C:\code / Z:\code):
#      tools\apache-maven-*\bin\mvn.cmd — prefer 3.9.6, else newest
#   4. mvn on PATH last
#
# PATH-only failed on Windows: Maven is often installed off PATH, and a packaged
# JDK 24 (Adoptium, etc.) may be on PATH while this project wants JDK 25.
# Hive paths are optional — without them, wrapper / mise / sdkman / PATH still work.
#
# JDK: keep JAVA_HOME when it already looks like 25; otherwise mise, sdkman, or
# tools\jdk-25.
#
#   .\scripts\afx-run.ps1
#   ./scripts/afx-run.sh

$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
if (-not (Test-Path (Join-Path $root 'pom.xml'))) {
    $root = (Get-Location).Path
}
Set-Location $root

function Get-HiveRoots {
    @(
        $env:code
        $env:CODE_ROOT
        'C:\code'
        'Z:\code'
    ) | Where-Object { $_ } | ForEach-Object { $_.TrimEnd('\', '/') } | Select-Object -Unique
}

function Test-JavaHomeLooks25 {
    param([string]$JdkHome)
    if (-not $JdkHome) { return $false }
    $exe = Join-Path $JdkHome 'bin\java.exe'
    $bin = Join-Path $JdkHome 'bin\java'
    if (-not ((Test-Path $exe) -or (Test-Path $bin))) { return $false }
    return ((Split-Path $JdkHome -Leaf) -match '25')
}

function Get-SdkmanDir {
    if ($env:SDKMAN_DIR) { return $env:SDKMAN_DIR.TrimEnd('\', '/') }
    $userHome = if ($env:USERPROFILE) { $env:USERPROFILE } else { $env:HOME }
    if (-not $userHome) { return $null }
    $dir = Join-Path $userHome '.sdkman'
    if (Test-Path $dir) { return $dir }
    return $null
}

function Get-SdkmanPin {
    param([string]$Tool)
    $rc = Join-Path $root '.sdkmanrc'
    if (-not (Test-Path $rc)) { return $null }
    foreach ($line in Get-Content $rc) {
        if ($line -match "^\s*$([regex]::Escape($Tool))=(.+)$") {
            return $Matches[1].Trim()
        }
    }
    return $null
}

function Find-Maven {
    $wrapperCmd = Join-Path $root 'mvnw.cmd'
    $wrapperSh = Join-Path $root 'mvnw'
    if (Test-Path $wrapperCmd) { return $wrapperCmd }
    if (Test-Path $wrapperSh) { return $wrapperSh }

    if (Test-Path (Join-Path $root 'mise.toml')) {
        $mise = Get-Command mise -ErrorAction SilentlyContinue
        if ($mise) {
            $which = & mise which mvn 2>$null
            if ($LASTEXITCODE -eq 0 -and $which) {
                $path = ($which | Select-Object -Last 1).ToString().Trim()
                if ($path -and (Test-Path $path)) { return $path }
            }
        }
    }

    if (Test-Path (Join-Path $root '.sdkmanrc')) {
        $sdk = Get-SdkmanDir
        if ($sdk) {
            $names = @()
            $pin = Get-SdkmanPin 'maven'
            if ($pin) { $names += $pin }
            $names += 'current'
            foreach ($n in $names) {
                $base = Join-Path $sdk "candidates\maven\$n\bin"
                foreach ($b in @('mvn.cmd', 'mvn')) {
                    $p = Join-Path $base $b
                    if (Test-Path $p) { return $p }
                }
            }
        }
    }

    $hiveMavens = @()
    foreach ($hive in Get-HiveRoots) {
        $tools = Join-Path $hive 'tools'
        if (-not (Test-Path $tools)) { continue }
        Get-ChildItem -Path $tools -Directory -ErrorAction SilentlyContinue -Filter 'apache-maven-*' |
            ForEach-Object {
                $cmd = Join-Path $_.FullName 'bin\mvn.cmd'
                $sh = Join-Path $_.FullName 'bin\mvn'
                $exe = $null
                if (Test-Path $cmd) { $exe = $cmd }
                elseif (Test-Path $sh) { $exe = $sh }
                if ($exe) {
                    $ver = $_.Name -replace '^apache-maven-', ''
                    $hiveMavens += [pscustomobject]@{ Version = $ver; Path = $exe }
                }
            }
    }
    $prefer = $hiveMavens | Where-Object { $_.Version -eq '3.9.6' } | Select-Object -First 1
    if ($prefer) { return $prefer.Path }
    $newest = $hiveMavens | Sort-Object {
        try { [version]$_.Version } catch { [version]'0.0.0' }
    } -Descending | Select-Object -First 1
    if ($newest) { return $newest.Path }

    $onPath = Get-Command mvn -ErrorAction SilentlyContinue
    if ($onPath) { return $onPath.Source }

    return $null
}

function Find-Jdk25Home {
    if (Test-Path (Join-Path $root 'mise.toml')) {
        $mise = Get-Command mise -ErrorAction SilentlyContinue
        if ($mise) {
            $which = & mise which java 2>$null
            if ($LASTEXITCODE -eq 0 -and $which) {
                $java = ($which | Select-Object -Last 1).ToString().Trim()
                if ($java -and (Test-Path $java)) {
                    $jdkHome = Split-Path (Split-Path $java -Parent) -Parent
                    if (Test-JavaHomeLooks25 $jdkHome) { return $jdkHome }
                }
            }
        }
    }

    if (Test-Path (Join-Path $root '.sdkmanrc')) {
        $sdk = Get-SdkmanDir
        $pin = Get-SdkmanPin 'java'
        if ($sdk) {
            $names = @()
            if ($pin) { $names += $pin }
            $names += 'current'
            foreach ($n in $names) {
                $jdkHome = Join-Path $sdk "candidates\java\$n"
                if (Test-JavaHomeLooks25 $jdkHome) { return $jdkHome }
            }
        }
    }

    foreach ($hive in Get-HiveRoots) {
        $candidate = Join-Path $hive 'tools\jdk-25'
        $exe = Join-Path $candidate 'bin\java.exe'
        $bin = Join-Path $candidate 'bin\java'
        if ((Test-Path $exe) -or (Test-Path $bin)) { return $candidate }
    }
    return $null
}

$mvn = Find-Maven
if (-not $mvn) {
    throw 'Maven not found. Install Maven, add a wrapper (mvnw), use mise/sdkman, put mvn on PATH, or install to tools\apache-maven-* under CODE_ROOT / C:\code / Z:\code.'
}

$knownJdk25 = Find-Jdk25Home
if ($knownJdk25 -and -not (Test-JavaHomeLooks25 $env:JAVA_HOME)) {
    $env:JAVA_HOME = $knownJdk25
}
if ($env:JAVA_HOME -and (Test-Path (Join-Path $env:JAVA_HOME 'bin'))) {
    $env:Path = "$(Join-Path $env:JAVA_HOME 'bin');$env:Path"
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    throw 'Java not found. Install JDK 25 and set JAVA_HOME (see mise.toml / .sdkmanrc), or install to tools\jdk-25 under CODE_ROOT.'
}

Write-Host "Using: $mvn"
if ($env:JAVA_HOME) {
    Write-Host "JAVA_HOME: $env:JAVA_HOME"
}
Write-Host "Compiling then running (kills stale target/classes)."
& $mvn -DskipTests -Plocal-run compile spring-boot:run @args
