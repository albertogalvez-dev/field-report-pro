# Contributing

Thanks for your interest in contributing!

## Development setup (Windows)

1) Configure the Android SDK:

```
./scripts/setup-android-sdk.ps1
```

2) Build:

```
./gradlew assembleDebug
```

## Tests and screenshots

```
./gradlew testDebugUnitTest
./gradlew verifyPaparazziDebug
./scripts/test-ci.ps1
```

To update screenshots:

```
./scripts/record-screenshots.ps1
```

## Notes

- Keep UI changes aligned with the Stitch design references.
- Avoid introducing non-determinism in screenshots.
