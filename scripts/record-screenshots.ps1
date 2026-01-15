$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
Push-Location $root

$buildRoot = Join-Path $env:USERPROFILE ".fieldreportpro-build"
$appBuildRoot = Join-Path $buildRoot "app"

$cleanupPaths = @(
    (Join-Path $root "app\build\test-results"),
    (Join-Path $root "app\build\reports\paparazzi"),
    (Join-Path $appBuildRoot "test-results"),
    (Join-Path $appBuildRoot "reports\paparazzi")
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

$destDir = Join-Path $root "screenshots"
New-Item -ItemType Directory -Force -Path $destDir | Out-Null

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
        Copy-Item -Force -Path $source.FullName -Destination (Join-Path $destDir "$name.png")
        Write-Host "Copied $name.png"
    } else {
        Write-Host "Missing $name.png"
    }
}

Pop-Location
