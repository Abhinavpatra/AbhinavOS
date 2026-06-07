# ATHLETEOS Implementation Plan

AthleteOS is a personal athletic operating system designed specifically for Abhinav to manage, execute, and log his athletic training.

## User Review Required

> [!IMPORTANT]
> **Aesthetic Approach**: We will implement a premium, dark-first visual cockpit designed for high-performance training:
> - **Colors**: Near-black background (`#0A0A0A`), dark gray card backgrounds (`#161616`), electric blue primary accent (`#00E5FF`), deep forest green success indicators (`#00E676`), and amber warnings (`#FFD600`).
> - **Typography**: Large, bold, highly readable numbers and targets suited for glanceability during intense workouts.
> - **Interactive Elements**: Rest timer floating in the top-right corner with physical-looking presets and auto-start upon set completion. Custom gesture controls: Single tap to complete set, double tap to complete exercise, long press to undo.

> [!WARNING]
> **Dependency Injection Strategy**: 
> While the README specifies **Hilt**, manual DI / Constructor Injection via Jetpack Compose's state/ViewModel provider factories is simpler, compiles significantly faster, and completely bypasses potential Kotlin/KSP/Hilt version compatibility issues on Kotlin 2.3.20. We will propose Hilt as a first class setup using `kapt` to ensure stability, but present Manual DI as an alternative if any compilation mismatch occurs.

## Open Questions

> [!IMPORTANT]
> **JSON Workout Program Seed**:
> Do you have a pre-existing JSON file for the 7-week training program, or should we create the JSON assets based on the structure specified in the README?
> *Decision*: We will bundle a complete, structured JSON asset representing the full 7-week training schedule with all days, exercises, targets, and sets based on the text program outlined in the README.

## Proposed Changes

### Build Configuration & Dependencies

#### [MODIFY] [libs.versions.toml](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/gradle/libs.versions.toml)
- Add Room, DataStore, WorkManager, Hilt, and Kotlinx Serialization.

#### [MODIFY] [build.gradle.kts (root)](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/build.gradle.kts)
- Register Hilt/KSP or Kapt plugins.

#### [MODIFY] [build.gradle.kts (app)](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/build.gradle.kts)
- Apply Kapt/KSP, Hilt, Room, DataStore, WorkManager.

---

### Core & Database Layer

#### [NEW] [Database Models & DAO](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/database/AthleteDatabase.kt)
- Define Room Entities for `Workout`, `Exercise`, `ExerciseSet`, `DailyArmorLog`, `MetricLog`, and `WorkoutNote`.
- Define DAOs for managing workout status, logging sets (planned vs actual), metrics tracking, and armor streak logging.

#### [NEW] [Database Seeding & Asset Manager](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/assets/program.json)
- Create `program.json` containing the full 7-week "Badminton Athleticism Block".
- Implement database initializer to detect first launch and seed tables from this JSON.

---

### Navigation & ViewModel Structure

#### [MODIFY] [NavigationKeys.kt](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/NavigationKeys.kt)
- Define navigation destinations for all core screens: Home, Weeks, WeekDetails, Day, WorkoutExecution, DailyArmor, Metrics, History, Settings.

#### [MODIFY] [Navigation.kt](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/Navigation.kt)
- Update NavDisplay route configuration to bind all destination screens to their respective ViewModels.

---

### Feature Screens (Compose UI)

#### [NEW] [Home Screen](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/features/home/HomeScreen.kt)
- Show "What am I doing today?", Tomorrow's teaser, current week timeline with highlighted day, and large "START WORKOUT" action.

#### [NEW] [Weeks & Week Details Screens](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/features/weeks/WeeksScreen.kt)
- Vertical week list with green/gray indicators.
- Week Details showing workout cards with completion % and duration.

#### [NEW] [Day & Workout Execution Screens](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/features/workout/WorkoutScreen.kt)
- Full list of exercises with completion checkmarks.
- Expandable exercise cards for target vs actual weight/reps input.
- Floating Rest Timer with customizable interval presets and auto-trigger on set complete.
- Complete gesture mapping (Single tap -> Set Complete, Double tap -> Exercise Complete, Long press -> Undo).

#### [NEW] [Daily Armor Block Screen](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/features/armor/DailyArmorScreen.kt)
- Interactive checklist for knee/core/neck stability routines.
- Persistent completion streak counter.

#### [NEW] [Metrics & Charts Screen](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/features/metrics/MetricsScreen.kt)
- Track Body (Weight, Body Fat, Waist), Recovery (Sleep, RHR, Water), Strength (Squat, Pullup, Press), and Athleticism (Vertical/Broad Jump, Sprint).
- Custom Canvas-based Line and Bar charts supporting 7d, 30d, 90d, and All Time filter ranges (eliminates version-heavy dependencies).
- PR celebration animations and automatic PR tracking.

#### [NEW] [History Screen](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/features/history/HistoryScreen.kt)
- Full calendar view showing workout days with Complete, Partial, and Missed indicators.
- Deep dive viewer for reviewing specific historical logged workouts.

#### [NEW] [Settings Screen](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/features/settings/SettingsScreen.kt)
- Adjust system theme, rest timer defaults, unit selectors, and Import/Export JSON backup utility.

---

### Background Services

#### [NEW] [WorkManager Notifications](file:///c:/Users/patra/Desktop/CompleteProjects/AbhinavOS/app/src/main/java/com/example/athleteos/features/notifications/NotificationWorker.kt)
- Schedule 6:00 PM today's training teaser and 9:00 PM "Workout Not Started" reminders.

---

## Verification Plan

### Automated Tests
- Run database seeding tests and DAO unit tests:
  ```bash
  ./gradlew.bat testDebugUnitTest
  ```

### Manual Verification
- Compile and run debug build.
- Verify onboarding-free seeding on launch.
- Test workout execution flow, rest timer countdown, daily checklist log, metrics entries, custom charts rendering, and settings import/export.
