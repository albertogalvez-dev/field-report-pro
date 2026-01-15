# Offline-First Architecture

```
[Compose UI Screens]
        |
   [ViewModels]
        |
 [ReportRepository]
   |       |        \
   |       |         \-> [WorkManager SyncWorker]
   |       |                 |-> updates Room + DataStore
   |       |
   |       -> [DataStore: settings + lastSyncAt]
   |
   -> [Room DB]
        |-> reports
        |-> attachments
        |-> timeline_events
```

Notes:
- The SyncWorker simulates uploads by updating sync progress in Room.
- UI consumes only repository flows for deterministic state.
