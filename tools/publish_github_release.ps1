param(
    [Parameter(Mandatory = $true)]
    [string]$RepoSlug,
    [switch]$CreateRepo,
    [string]$MetadataOutputPath = ""
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

function Try-LoadGitHubTokenFromCredentialManager {
    try {
        $credential = "protocol=https`nhost=github.com`n`n" | git credential-manager get 2>$null
        if (-not $credential) {
            return $false
        }
        $password = $credential | Select-String '^password=' | ForEach-Object { $_.Line } | Select-Object -First 1
        if (-not $password) {
            return $false
        }
        $token = $password.Substring("password=".Length).Trim()
        if (-not $token) {
            return $false
        }
        $env:GH_TOKEN = $token
        $env:GITHUB_TOKEN = $token
        return $true
    } catch {
        return $false
    }
}

function Test-GitHubCliAccess {
    try {
        gh api user 1>$null 2>$null
        return $LASTEXITCODE -eq 0
    } catch {
        return $false
    }
}

function Ensure-GitHubAuth {
    if (Test-GitHubCliAccess) {
        return
    }
    if (Try-LoadGitHubTokenFromCredentialManager -and (Test-GitHubCliAccess)) {
        return
    }
    throw "GitHub CLI has not logged in yet. Run 'gh auth login' first, or ensure git-credential-manager already stores a usable GitHub token."
}

function Resolve-MetadataPath([string]$preferredPath, [string]$defaultPath) {
    if ($preferredPath) {
        return [System.IO.Path]::GetFullPath($preferredPath)
    }
    return $defaultPath
}

function New-MetadataUploadCopy([string]$sourcePath) {
    $uploadDir = Join-Path $root "tmp\release_upload"
    if (-not (Test-Path $uploadDir)) {
        New-Item -ItemType Directory -Path $uploadDir -Force | Out-Null
    }
    $uploadPath = Join-Path $uploadDir "network_quiz_update.json"
    Copy-Item -LiteralPath $sourcePath -Destination $uploadPath -Force
    return $uploadPath
}

function Ensure-GitRepository {
    if (-not (Test-Path .git)) {
        git init -b main | Out-Null
    }
}

function Has-GitCommit {
    try {
        git rev-parse --verify HEAD 1>$null 2>$null
        return $LASTEXITCODE -eq 0
    } catch {
        return $false
    }
}

function Ensure-InitialCommit {
    if (Has-GitCommit) {
        return
    }
    git add .
    git commit -m "Initial commit: Network Quiz APK" | Out-Null
}

function Has-OriginRemote {
    try {
        git remote get-url origin 1>$null 2>$null
        return $LASTEXITCODE -eq 0
    } catch {
        return $false
    }
}

function Ensure-ReleaseNotes([string]$path, [string]$versionName) {
    if (Test-Path $path) {
        return
    }
    @"
# v$versionName

- Fill in the release notes for this version here.
"@ | Set-Content -Path $path -Encoding UTF8
}

function Read-JsonFile([string]$path) {
    return Get-Content -Raw -Encoding UTF8 $path | ConvertFrom-Json
}

function Test-ReleaseExists([string]$repoSlug, [string]$tag) {
    try {
        gh release view $tag --repo $repoSlug 1>$null 2>$null
        return $LASTEXITCODE -eq 0
    } catch {
        return $false
    }
}

function Get-ReleaseApi([string]$repoSlug, [string]$tag) {
    $json = gh api "repos/$repoSlug/releases/tags/$tag"
    return $json | ConvertFrom-Json
}

function Find-ApkAssetUrl($release, [string]$preferredName) {
    $apkAsset = $null
    if ($preferredName) {
        $apkAsset = $release.assets | Where-Object { $_.name -eq $preferredName } | Select-Object -First 1
    }
    if (-not $apkAsset) {
        $apkAsset = $release.assets | Where-Object { $_.name -like "*.apk" } | Select-Object -First 1
    }
    if (-not $apkAsset) {
        throw "No APK asset was found in release $($release.tag_name)."
    }
    return $apkAsset.browser_download_url
}

function Invoke-GhWithRetry([scriptblock]$command, [string]$errorMessage, [int]$maxAttempts = 3, [int]$sleepSeconds = 3) {
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

function Invoke-JsDelivrMetadataPurge([string]$repoSlug) {
    $purgeUrl = "https://purge.jsdelivr.net/gh/$repoSlug@main/release/network_quiz_update.json"
    try {
        $response = Invoke-WebRequest -Uri $purgeUrl -UseBasicParsing -TimeoutSec 30
        if ($response.StatusCode -ge 200 -and $response.StatusCode -lt 300) {
            Write-Host "jsDelivr metadata purge requested:" $purgeUrl
            return
        }
        Write-Warning "jsDelivr metadata purge returned HTTP $($response.StatusCode): $purgeUrl"
    } catch {
        Write-Warning ("jsDelivr metadata purge failed: " + $_.Exception.Message)
    }
}

Ensure-GitHubAuth
Ensure-GitRepository
Ensure-InitialCommit

$manifest = Get-Content .\app\src\main\AndroidManifest.xml -Raw -Encoding UTF8
$versionName = [regex]::Match($manifest, 'android:versionName="([^"]+)"').Groups[1].Value
if (-not $versionName) {
    throw "Unable to read versionName from AndroidManifest.xml"
}

$tag = "v$versionName"
$notes = Join-Path $root "release\RELEASE_NOTES.md"
$defaultMeta = Join-Path $root "release\network_quiz_update.json"
$meta = Resolve-MetadataPath -preferredPath $MetadataOutputPath -defaultPath $defaultMeta

Ensure-ReleaseNotes -path $notes -versionName $versionName

python .\tools\build_network_quiz_apk.py
python .\tools\generate_release_metadata.py --release-notes-file $notes --repo-slug $RepoSlug --output $meta

$metaInfo = Read-JsonFile $meta
$apk = Join-Path $root ("build\out\" + $metaInfo.apkFileName)
if (-not (Test-Path $apk)) {
    throw "APK not found: $apk"
}

if ($CreateRepo -and -not (Has-OriginRemote)) {
    Invoke-GhWithRetry { gh repo create $RepoSlug --public --source . --remote origin --push } "Failed to create or push GitHub repo $RepoSlug."
}

if (Test-ReleaseExists -repoSlug $RepoSlug -tag $tag) {
    Invoke-GhWithRetry { gh release edit $tag --repo $RepoSlug --title $tag --notes-file $notes } "Failed to edit GitHub Release $tag."
    Invoke-GhWithRetry { gh release upload $tag $apk --repo $RepoSlug --clobber } "Failed to upload APK asset for GitHub Release $tag."
} else {
    Invoke-GhWithRetry { gh release create $tag $apk --repo $RepoSlug --title $tag --notes-file $notes } "Failed to create GitHub Release $tag."
}

$release = Get-ReleaseApi -repoSlug $RepoSlug -tag $tag
$apkDownloadUrl = Find-ApkAssetUrl -release $release -preferredName $metaInfo.apkFileName

python .\tools\generate_release_metadata.py --release-notes-file $notes --repo-slug $RepoSlug --release-html-url $release.html_url --apk-download-url $apkDownloadUrl --output $meta
$metadataUploadPath = New-MetadataUploadCopy -sourcePath $meta
try {
    Invoke-GhWithRetry { gh release upload $tag $metadataUploadPath --repo $RepoSlug --clobber } "Failed to upload update metadata asset for GitHub Release $tag."
} finally {
    if (Test-Path $metadataUploadPath) {
        Remove-Item $metadataUploadPath -Force -ErrorAction SilentlyContinue
    }
}

Invoke-JsDelivrMetadataPurge -repoSlug $RepoSlug

$releaseUrl = $release.html_url
Write-Host "Release published:" $tag
Write-Host "Repository:" $RepoSlug
Write-Host "Release URL:" $releaseUrl
