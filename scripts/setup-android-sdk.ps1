$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$paths = @()

if ($env:ANDROID_SDK_ROOT) { $paths += $env:ANDROID_SDK_ROOT }
if ($env:ANDROID_HOME) { $paths += $env:ANDROID_HOME }
if ($env:LOCALAPPDATA) { $paths += (Join-Path $env:LOCALAPPDATA "Android\Sdk") }
$paths += "C:\Android\Sdk"

$found = $null
foreach ($path in $paths) {
    if (-not $path) { continue }
    $candidate = Resolve-Path -Path $path -ErrorAction SilentlyContinue
    if (-not $candidate) { continue }
    $sdkPath = $candidate.Path
    if ((Test-Path (Join-Path $sdkPath "platforms")) -and (Test-Path (Join-Path $sdkPath "platform-tools"))) {
        $found = $sdkPath
        break
    }
}

if (-not $found) {
    Write-Host "Android SDK not found."
    Write-Host "Install Android Studio or the Android command line tools."
    Write-Host "Then set ANDROID_SDK_ROOT or ANDROID_HOME, or install to:"
    Write-Host "  $env:LOCALAPPDATA\Android\Sdk"
    exit 1
}

$sdkEscaped = $found -replace "\\", "\\\\"
$localProps = Join-Path $root "local.properties"
"sdk.dir=$sdkEscaped" | Set-Content -Path $localProps

Write-Host "SDK FOUND at: $found"
Write-Host "Wrote local.properties"
