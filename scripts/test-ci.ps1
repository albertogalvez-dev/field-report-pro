$ErrorActionPreference = "Stop"

Write-Host "Stopping Gradle daemons..."
& ./gradlew --stop | Out-Host

Write-Host "Assembling debug..."
& ./gradlew assembleDebug | Out-Host

Write-Host "Running unit tests (debug)..."
& ./gradlew testDebugUnitTest | Out-Host

Write-Host "Verifying Paparazzi snapshots..."
& ./gradlew verifyPaparazziDebug | Out-Host

Write-Host "OK"
