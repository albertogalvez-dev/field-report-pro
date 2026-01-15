$ErrorActionPreference = "Stop"

Write-Host "Stopping Gradle daemons..."
& ./gradlew --stop | Out-Host

$paths = @(
    "app/build",
    "build",
    "$env:USERPROFILE\\.fieldreportpro-build"
)
$maxAttempts = 5

foreach ($path in $paths) {
    $attempt = 1
    while ($attempt -le $maxAttempts) {
        try {
            if (-not (Test-Path $path)) {
                Write-Host "Skip $path (not found)"
                break
            }
            Remove-Item -Recurse -Force $path -ErrorAction Stop
            Write-Host "Deleted $path"
            break
        } catch {
            if ($attempt -ge $maxAttempts) {
                Write-Host "Failed to delete $path"
                Write-Host "OneDrive may be locking files. Close IDE, pause OneDrive sync or move repo outside OneDrive."
                break
            }
            Start-Sleep -Milliseconds 500
            $attempt++
        }
    }
}

Write-Host "Cleanup complete."
