param()

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

function Write-HookLog([string]$message) {
    $dir = Join-Path $root "build\out"
    if (-not (Test-Path $dir)) {
        New-Item -ItemType Directory -Path $dir | Out-Null
    }
    $line = ("[{0}] {1}" -f (Get-Date -Format "yyyy-MM-dd HH:mm:ss"), $message)
    Add-Content -Path (Join-Path $dir "auto_release_hook.log") -Value $line -Encoding UTF8
}

function Get-GitConfigValue([string]$key) {
    try {
        $value = git config --get $key 2>$null
        if ($LASTEXITCODE -ne 0) {
            return ""
        }
        return ($value | Out-String).Trim()
    } catch {
        return ""
    }
}

function Get-BlockingStatusText {
    try {
        $lines = git status --porcelain
        if ($LASTEXITCODE -ne 0) {
            return "git status failed"
        }
        $blocking = [System.Collections.Generic.List[string]]::new()
        foreach ($line in ($lines | Out-String).Trim().Split([Environment]::NewLine, [System.StringSplitOptions]::RemoveEmptyEntries)) {
            $trimmed = $line.Trim()
            if (-not $trimmed) {
                continue
            }
            if ($trimmed -match 'release/(exam-prep-handbook-update|network_quiz_update)\.json$') {
                continue
            }
            $blocking.Add($trimmed)
        }
        return ($blocking | Out-String).Trim()
    } catch {
        return "git status failed"
    }
}

if ($env:CODEX_AUTO_RELEASE_RUNNING -eq "1") {
    exit 0
}

$enabled = Get-GitConfigValue "examprep.autoRelease"
if (-not $enabled) {
    $enabled = Get-GitConfigValue "networkquiz.autoRelease"
}
if ($enabled -ne "true") {
    exit 0
}

$subject = (git log -1 --pretty=%s | Out-String).Trim()
if (-not $subject) {
    exit 0
}
if ($subject -like "Release prep:*" -or $subject -match '\[skip-release\]') {
    exit 0
}

$dirty = Get-BlockingStatusText
if ($dirty.Length -gt 0) {
    Write-HookLog("Skip auto release because worktree is dirty after commit: $subject :: $dirty")
    exit 0
}

try {
    $env:CODEX_AUTO_RELEASE_RUNNING = "1"
    & powershell -NoProfile -ExecutionPolicy Bypass -File (Join-Path $root "tools\release_after_fix.ps1") -FixSummary $subject
    Write-HookLog("Auto release succeeded for commit: $subject")

    try {
        & powershell -NoProfile -ExecutionPolicy Bypass -File (Join-Path $root "tools\sync_latest_apk_to_emulators.ps1")
        if ($LASTEXITCODE -eq 0) {
            Write-HookLog("Emulator sync completed after auto release: $subject")
        } else {
            Write-HookLog("Emulator sync exited with code $LASTEXITCODE after auto release: $subject")
        }
    } catch {
        Write-HookLog("Emulator sync failed after auto release: $subject :: $($_.Exception.Message)")
    }
} catch {
    Write-HookLog("Auto release failed for commit: $subject :: $($_.Exception.Message)")
    Write-Host "Auto release failed:" $_.Exception.Message
}
