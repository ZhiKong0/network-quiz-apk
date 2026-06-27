param(
    [string]$RepoSlug = "",
    [string]$Branch = "",
    [string]$FixSummary = "",
    [switch]$CreateRepo
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

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

function Get-ManifestInfo {
    $manifest = Get-Content .\app\src\main\AndroidManifest.xml -Raw -Encoding UTF8
    $versionName = [regex]::Match($manifest, 'android:versionName="([^"]+)"').Groups[1].Value
    $versionCode = [int][regex]::Match($manifest, 'android:versionCode="(\d+)"').Groups[1].Value
    if (-not $versionName -or -not $versionCode) {
        throw "Unable to read version info from AndroidManifest.xml"
    }
    return @{
        VersionName = $versionName
        VersionCode = $versionCode
    }
}

function Has-OriginRemote {
    try {
        git remote get-url origin 1>$null 2>$null
        return $LASTEXITCODE -eq 0
    } catch {
        return $false
    }
}

function Add-ReleaseNoteBullet([string]$path, [string]$bullet) {
    if (-not (Test-Path $path)) {
        return
    }
    $text = Get-Content -Raw -Encoding UTF8 $path
    if ($text -match [regex]::Escape($bullet)) {
        return
    }
    $lines = [System.Collections.Generic.List[string]]::new()
    foreach ($line in ($text -split "`r?`n")) {
        $lines.Add($line)
    }
    $insertIndex = 1
    if ($lines.Count -ge 2 -and $lines[1].Trim().Length -eq 0) {
        $insertIndex = 2
    }
    $lines.Insert($insertIndex, $bullet)
    $updated = [string]::Join("`r`n", $lines).TrimEnd() + "`r`n"
    [System.IO.File]::WriteAllText($path, $updated, [System.Text.UTF8Encoding]::new($false))
}

function Ensure-CleanWorktree {
    $status = git status --porcelain
    if ($LASTEXITCODE -ne 0) {
        throw "git status failed"
    }
    $text = ($status | Out-String).Trim()
    if ($text.Length -gt 0) {
        throw "Auto release requires a clean worktree. Commit or stash remaining changes first."
    }
}

function Invoke-NativeOrThrow([scriptblock]$command, [string]$errorMessage) {
    & $command
    if ($LASTEXITCODE -ne 0) {
        throw $errorMessage
    }
}

function Invoke-NativeWithRetry([scriptblock]$command, [string]$errorMessage, [int]$maxAttempts = 3, [int]$sleepSeconds = 3) {
    for ($attempt = 1; $attempt -le $maxAttempts; $attempt++) {
        & $command
        if ($LASTEXITCODE -eq 0) {
            return
        }
        if ($attempt -lt $maxAttempts) {
            Start-Sleep -Seconds $sleepSeconds
        }
    }
    throw $errorMessage
}

if (-not $RepoSlug) {
    $RepoSlug = Get-GitConfigValue "examprep.releaseRepo"
}
if (-not $RepoSlug) {
    $RepoSlug = Get-GitConfigValue "networkquiz.releaseRepo"
}
if (-not $RepoSlug) {
    throw "Release repo slug is not configured. Run .\tools\install_auto_release_hook.ps1 -RepoSlug owner/repo first."
}

if (-not $Branch) {
    $Branch = (git rev-parse --abbrev-ref HEAD | Out-String).Trim()
}
if (-not $Branch) {
    throw "Unable to determine current git branch."
}

if (-not $FixSummary) {
    $FixSummary = (git log -1 --pretty=%s | Out-String).Trim()
}
if (-not $FixSummary) {
    $FixSummary = "Fix update"
}

if ($FixSummary -like "Release prep:*" -or $FixSummary -match '\[skip-release\]') {
    Write-Host "Skip auto release for commit:" $FixSummary
    exit 0
}

Ensure-CleanWorktree

$before = Get-ManifestInfo
python .\tools\bump_exam_prep_handbook_version.py --increment patch
$after = Get-ManifestInfo
$versionName = $after.VersionName
$tag = "v$versionName"
$notesPath = Join-Path $root "release\RELEASE_NOTES.md"
$metadataTempPath = Join-Path $root "tmp\auto_release_metadata_$versionName.json"

Add-ReleaseNoteBullet -path $notesPath -bullet ("- Auto release after fix: " + $FixSummary)
python .\tools\build_exam_prep_handbook_apk.py
python .\tools\generate_release_metadata.py --release-notes-file $notesPath

git add .\app\src\main\AndroidManifest.xml .\release\RELEASE_NOTES.md .\release\exam-prep-handbook-update.json .\release\network_quiz_update.json

$env:CODEX_AUTO_RELEASE_RUNNING = "1"
Invoke-NativeOrThrow { git commit -m "Release prep: v$versionName" | Out-Null } "Failed to create release prep commit for $tag."
Invoke-NativeOrThrow { git tag $tag } "Failed to create git tag $tag."

$hasOrigin = Has-OriginRemote
if ($hasOrigin) {
    Invoke-NativeWithRetry { git push origin $Branch | Out-Null } "Failed to push branch $Branch to origin."
    Invoke-NativeWithRetry { git push origin $tag | Out-Null } "Failed to push tag $tag to origin."
}

try {
    $publishScript = Join-Path $root "tools\publish_github_release.ps1"
    if ($CreateRepo) {
        & $publishScript -RepoSlug $RepoSlug -CreateRepo -MetadataOutputPath $metadataTempPath
    } else {
        & $publishScript -RepoSlug $RepoSlug -MetadataOutputPath $metadataTempPath
    }
} finally {
    if (Test-Path $metadataTempPath) {
        Remove-Item $metadataTempPath -Force -ErrorAction SilentlyContinue
    }
}

Write-Host "Auto release completed:" $tag
Write-Host "Repo:" $RepoSlug
