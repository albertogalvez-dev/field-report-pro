# Troubleshooting

## Windows + OneDrive file locks

If you see errors like:

```
Unable to delete directory ... app\build\...
AccessDeniedException ... OneDrive
```

OneDrive (or an IDE) is likely holding files in `app/build`.

Recommended fixes:

1) Move the repo outside OneDrive (example: `C:\dev\FieldReportPro`).
2) Or pause OneDrive sync during builds/tests.
3) Close Android Studio and any file watchers.
4) Run `./scripts/clean-windows.ps1` and retry the Gradle command.

Notes:

- Build outputs are redirected to `%USERPROFILE%\.fieldreportpro-build` to reduce OneDrive locks.
