# Field Report Pro

## Screenshots

| Home (Light) | Home (Dark) | Empty State |
| --- | --- | --- |
| ![Home Light](screenshots/home_light.png) | ![Home Dark](screenshots/home_dark.png) | ![Empty State](screenshots/empty_state.png) |

| New Report | Report Detail | Sync Center |
| --- | --- | --- |
| ![Form](screenshots/form.png) | ![Detail](screenshots/detail.png) | ![Sync](screenshots/sync.png) |

| Settings (Dark) | Annotation (Dark) |
| --- | --- |
| ![Settings Dark](screenshots/settings_dark.png) | ![Annotation Dark](screenshots/annotate_dark.png) |

## Build

1) Set up the Android SDK:

```
./scripts/setup-android-sdk.ps1
```

2) Build:

```
./gradlew assembleDebug
```

3) Record screenshots:

```
./scripts/record-screenshots.ps1
```
