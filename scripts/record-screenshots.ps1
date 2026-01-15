param(
    [string]$Confirm
)

$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
Push-Location $root

$buildRoot = Join-Path $env:USERPROFILE ".fieldreportpro-build"
$appBuildRoot = Join-Path $buildRoot "app"

$stagingDir = Join-Path $root ".screenshots_staging"

$cleanupPaths = @(
    (Join-Path $root "app\build\test-results"),
    (Join-Path $root "app\build\reports\paparazzi"),
    (Join-Path $appBuildRoot "test-results"),
    (Join-Path $appBuildRoot "reports\paparazzi"),
    $stagingDir
)
foreach ($path in $cleanupPaths) {
    if (Test-Path $path) {
        Remove-Item -Recurse -Force -ErrorAction SilentlyContinue $path
    }
}

$tasksOutput = & .\gradlew tasks --all
$taskName = $null

$debugMatch = $tasksOutput | Select-String -Pattern "recordPaparazziDebug" | Select-Object -First 1
if ($debugMatch) {
    $taskName = $debugMatch.ToString().Trim().Split(' ')[0]
}

if (-not $taskName) {
    $recordMatch = $tasksOutput | Select-String -Pattern "recordPaparazzi" | Select-Object -First 1
    if ($recordMatch) {
        $taskName = $recordMatch.ToString().Trim().Split(' ')[0]
    }
}

if (-not $taskName) {
    Write-Host "No Paparazzi record task found."
    Pop-Location
    exit 1
}

Write-Host "Running $taskName"
& .\gradlew $taskName

New-Item -ItemType Directory -Force -Path $stagingDir | Out-Null

$sourceRoots = @(
    (Join-Path $root "app\src\test\snapshots"),
    (Join-Path $root "app\build\paparazzi"),
    (Join-Path $root "app\build\reports\paparazzi"),
    (Join-Path $appBuildRoot "paparazzi"),
    (Join-Path $appBuildRoot "reports\paparazzi")
)
$sourceRoots = $sourceRoots | Where-Object { Test-Path $_ }

if (-not $sourceRoots) {
    Write-Host "No Paparazzi output directory found."
    Pop-Location
    exit 1
}

$names = @(
    "home_light",
    "home_dark",
    "empty_state",
    "form",
    "detail",
    "sync",
    "settings_dark",
    "annotate_dark"
)

foreach ($name in $names) {
    $pattern = "*_${name}.png"
    $source = Get-ChildItem -Recurse -Filter $pattern -Path $sourceRoots | Select-Object -First 1
    if ($source) {
        Copy-Item -Force -Path $source.FullName -Destination (Join-Path $stagingDir "$name.png")
        Write-Host "Staged $name.png"
    } else {
        Write-Host "Missing $name.png"
    }
}

if (-not (Test-Path $stagingDir)) {
    Write-Host "No staging directory created."
    Pop-Location
    exit 1
}

$previewPath = Join-Path $stagingDir "preview.html"
$html = @"
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1" />
  <title>Field Report Pro — Screenshot Preview</title>
  <style>
    :root { color-scheme: light; }
    body { font-family: -apple-system, Segoe UI, Roboto, sans-serif; margin: 24px; color: #1f1f1f; }
    h1 { font-size: 20px; margin-bottom: 6px; }
    p { color: #5a5a5a; margin-top: 0; }
    .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(220px, 1fr)); gap: 16px; }
    .card { border: 1px solid #e5e5e5; border-radius: 12px; padding: 12px; background: #fff; }
    img { width: 100%; height: auto; border-radius: 8px; display: block; }
    .label { margin-top: 8px; font-size: 12px; color: #444; text-align: center; }
    .hint { margin-top: 16px; font-size: 13px; color: #3d3d3d; }
  </style>
</head>
<body>
  <h1>Screenshot Preview</h1>
  <p>Review the images below. If they look good, confirm to copy them into <code>/screenshots</code>.</p>
  <div class="grid">
    <div class="card"><a href="home_light.png"><img src="home_light.png" alt="home_light" /></a><div class="label">home_light</div></div>
    <div class="card"><a href="home_dark.png"><img src="home_dark.png" alt="home_dark" /></a><div class="label">home_dark</div></div>
    <div class="card"><a href="empty_state.png"><img src="empty_state.png" alt="empty_state" /></a><div class="label">empty_state</div></div>
    <div class="card"><a href="form.png"><img src="form.png" alt="form" /></a><div class="label">form</div></div>
    <div class="card"><a href="detail.png"><img src="detail.png" alt="detail" /></a><div class="label">detail</div></div>
    <div class="card"><a href="sync.png"><img src="sync.png" alt="sync" /></a><div class="label">sync</div></div>
    <div class="card"><a href="settings_dark.png"><img src="settings_dark.png" alt="settings_dark" /></a><div class="label">settings_dark</div></div>
    <div class="card"><a href="annotate_dark.png"><img src="annotate_dark.png" alt="annotate_dark" /></a><div class="label">annotate_dark</div></div>
  </div>
  <div class="hint">If everything looks correct, confirm in the terminal to update <code>/screenshots</code>.</div>
</body>
</html>
"@
Set-Content -Path $previewPath -Value $html -Encoding UTF8

Write-Host "Preview generated at $previewPath"
Start-Process $previewPath

if (-not $Confirm) {
    $Confirm = Read-Host "Copy staged screenshots into /screenshots and overwrite existing? (Y/N)"
}

if ($Confirm -match '^[Yy]') {
    $destDir = Join-Path $root "screenshots"
    New-Item -ItemType Directory -Force -Path $destDir | Out-Null
    Copy-Item -Force -Path (Join-Path $stagingDir "*.png") -Destination $destDir
    Write-Host "Updated /screenshots"
} else {
    Write-Host "Kept staging only"
}

Pop-Location
