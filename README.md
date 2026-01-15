# Field Report Pro
[![CI](https://github.com/albertogalvez-dev/field-report-pro/actions/workflows/ci.yml/badge.svg)](https://github.com/albertogalvez-dev/field-report-pro/actions/workflows/ci.yml)

Offline-first field incident reporting app for Android.

## Key Features

- Offline-first storage with Room (reports, attachments, timeline)
- Settings persistence via DataStore
- WorkManager sync queue with progress
- Photo attachments (gallery/camera) up to 3 per report
- Photo annotation (circle/arrow/rect) saving annotated PNG
- Deterministic screenshot pipeline with Paparazzi

## Screenshots

| Home (Light) | Home (Dark) | Empty State |
| --- | --- | --- |
| [![Home Light](screenshots/home_light.png)](screenshots/home_light.png) | [![Home Dark](screenshots/home_dark.png)](screenshots/home_dark.png) | [![Empty State](screenshots/empty_state.png)](screenshots/empty_state.png) |

| New Report | Report Detail | Sync Center |
| --- | --- | --- |
| [![Form](screenshots/form.png)](screenshots/form.png) | [![Detail](screenshots/detail.png)](screenshots/detail.png) | [![Sync](screenshots/sync.png)](screenshots/sync.png) |

| Settings (Dark) | Annotation (Dark) |
| --- | --- |
| [![Settings Dark](screenshots/settings_dark.png)](screenshots/settings_dark.png) | [![Annotation Dark](screenshots/annotate_dark.png)](screenshots/annotate_dark.png) |

## Architecture

- MVVM + Repository
- Room + DataStore + WorkManager
- See `docs/ARCHITECTURE.md`

## Quick start (Windows)

1) Configure the Android SDK:

```
./scripts/setup-android-sdk.ps1
```

2) Build:

```
./gradlew assembleDebug
```

3) Generate screenshots:

```
./scripts/record-screenshots.ps1
```

4) Tests:

```
./gradlew testDebugUnitTest
./gradlew verifyPaparazziDebug
./scripts/test-ci.ps1
```

## CI and reports

- CI runs `assembleDebug`, `testDebugUnitTest`, and `verifyPaparazziDebug`.
- Paparazzi reports are uploaded as a workflow artifact named paparazzi-report (Actions -> run -> Artifacts).

## Troubleshooting

See `docs/TROUBLESHOOTING.md`. Build outputs are stored in `%USERPROFILE%\.fieldreportpro-build`.

## Roadmap

- Multi-select attachment management
- Sync scheduling policies
- Export/share report summary

