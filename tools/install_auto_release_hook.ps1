param(
    [string]$RepoSlug = "ZhiKong0/exam-prep-handbook-apk"
)

$ErrorActionPreference = "Stop"

$root = Split-Path -Parent $PSScriptRoot
Set-Location $root

git config core.hooksPath .githooks
git config examprep.autoRelease true
git config examprep.releaseRepo $RepoSlug
git config networkquiz.autoRelease true
git config networkquiz.releaseRepo $RepoSlug

Write-Host "Auto release hook installed."
Write-Host "Repo slug:" $RepoSlug
Write-Host "Hooks path:" (git config core.hooksPath)
