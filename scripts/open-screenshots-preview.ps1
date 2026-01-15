$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$previewPath = Join-Path $root ".screenshots_staging\\preview.html"

if (Test-Path $previewPath) {
    Start-Process $previewPath
    Write-Host "Opened $previewPath"
} else {
    Write-Host "No preview found. Run ./scripts/record-screenshots.ps1 first."
}
