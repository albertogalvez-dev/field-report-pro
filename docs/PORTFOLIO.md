# Field Report Pro — Offline-first incident reporting (Android)

## TL;DR

Field Report Pro is a polished Android app for capturing field incidents with offline-first storage, sync queueing, and photo annotation. It is designed for clear demos and consistent UI screenshots.

## Stack

- Kotlin, Jetpack Compose (Material 3)
- Room, DataStore, WorkManager
- Coil
- Paparazzi (deterministic screenshots)

## What I built

- End-to-end offline-first data layer with sync states and progress.
- Photo attachments via gallery/camera with a 3-image cap.
- In-app annotation (circle/arrow/rect) that saves an annotated PNG.
- UI system aligned to Stitch designs with reusable components.
- CI pipeline for build, unit tests, and Paparazzi verification.

## Highlights

- Offline-first persistence with reliable UI states.
- Sync queue simulation with progress and retry paths.
- Deterministic screenshots for fast iteration and portfolio use.
- Clean, photo-ready screens designed for demos.

## What makes it different

- True offline-first UX with meaningful sync feedback.
- Annotation workflow that produces a real edited image.
- Snapshot testing to keep visuals consistent over time.

## Links

- Repo: https://github.com/albertogalvez-dev/field-report-pro
- CI workflow: https://github.com/albertogalvez-dev/field-report-pro/actions/workflows/ci.yml
- Demo video: (add link)

## Screenshots

See the grid in `/screenshots`.
