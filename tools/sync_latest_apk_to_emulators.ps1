param(
    [string]$ApkPath = ""
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

function Resolve-AdbPath {
    $candidates = [System.Collections.Generic.List[string]]::new()

    foreach ($sdkRoot in @($env:ANDROID_SDK_ROOT, $env:ANDROID_HOME, (Join-Path $env:LOCALAPPDATA "Android\Sdk"))) {
        if (-not [string]::IsNullOrWhiteSpace($sdkRoot)) {
            $candidates.Add((Join-Path $sdkRoot "platform-tools\adb.exe"))
        }
    }

    try {
        $command = Get-Command adb -ErrorAction Stop
        if ($command -and $command.Source) {
            $candidates.Add($command.Source)
        }
    } catch {
    }

    foreach ($candidate in $candidates) {
        if ($candidate -and (Test-Path $candidate)) {
            return $candidate
        }
    }

    throw "adb.exe not found. Checked ANDROID_SDK_ROOT / ANDROID_HOME / LOCALAPPDATA Android SDK."
}

function Get-ManifestVersionLabel {
    $manifestPath = Join-Path $root "app\src\main\AndroidManifest.xml"
    $manifest = Get-Content $manifestPath -Raw -Encoding UTF8
    $versionName = [regex]::Match($manifest, 'android:versionName="([^"]+)"').Groups[1].Value
    $versionCode = [regex]::Match($manifest, 'android:versionCode="(\d+)"').Groups[1].Value
    if (-not $versionName -or -not $versionCode) {
        throw "Unable to read version info from AndroidManifest.xml"
    }
    return "$versionName ($versionCode)"
}

function Get-RunningEmulators([string]$adbPath) {
    $lines = & $adbPath devices
    if ($LASTEXITCODE -ne 0) {
        throw "adb devices failed."
    }

    $devices = [System.Collections.Generic.List[string]]::new()
    foreach ($line in $lines) {
        if ($line -match '^(emulator-\d+)\s+device$') {
            $devices.Add($Matches[1])
        }
    }
    return $devices
}

if (-not $ApkPath) {
    $ApkPath = Join-Path $root "build\out\exam-prep-handbook.apk"
}

if (-not (Test-Path $ApkPath)) {
    throw "APK not found: $ApkPath"
}

$adbPath = Resolve-AdbPath
$targetVersion = Get-ManifestVersionLabel
$emulators = Get-RunningEmulators -adbPath $adbPath

if ($emulators.Count -eq 0) {
    Write-Host "No running emulators found. Skip APK sync."
    exit 0
}

foreach ($device in $emulators) {
    Write-Host "Installing $targetVersion to $device ..."
    & $adbPath -s $device install -r $ApkPath
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to install APK on $device"
    }

    $packageInfo = & $adbPath -s $device shell dumpsys package com.dz.networkquiz
    if ($LASTEXITCODE -ne 0) {
        throw "Failed to verify installed package on $device"
    }

    $versionCodeLine = ($packageInfo | Select-String 'versionCode=' | Select-Object -First 1).Line.Trim()
    $versionNameLine = ($packageInfo | Select-String 'versionName=' | Select-Object -First 1).Line.Trim()
    Write-Host "Synced $device => $versionNameLine / $versionCodeLine"
}
