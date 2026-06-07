# AthleteOS Tasks Checklist

- `[x]` Configure dependencies & Gradle build setup (Room, DataStore, WorkManager, Serialization, Hilt)
- `[x]` Define Database Entities, DAOs, and Database class (`AthleteDatabase.kt`)
- `[x]` Add `program.json` asset and database seeding mechanism
- `[x]` Implement DataStore repository for settings and daily streaks
- `[x]` Update Navigation structures & NavigationKeys
- `[x]` Implement Core screens & ViewModels:
  - `[x]` Home Screen (Today's task, week timeline, Tomorrow's preview)
  - `[x]` Weeks List & Week Details Screen
  - `[x]` Day View & Workout Execution Screen (Rest timer, actual weight/reps input, gestures)
  - `[x]` Daily Armor Checklist & Streak tracker
  - `[x]` Metrics Screen (PR tracking, Custom Canvas charts)
  - `[x]` History Screen (Calendar, workout deep-dive)
  - `[x]` Settings Screen (Dark/Light mode, rest defaults, units, export/import data)
- `[x]` Implement WorkManager background notification worker
- `[x]` Compile, run verification tests, and verify app builds successfully
