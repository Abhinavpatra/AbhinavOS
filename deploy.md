# Deploy

**Prerequisites:** Android 7.0+ (API 24), developer mode & USB debugging enabled on phone.

## 1. Build the APK

```powershell
.\gradlew assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

## 2. Install via USB

```
adb install app/build/outputs/apk/debug/app-debug.apk
```

If `adb` is not in PATH, use the one bundled with Android Studio:

```
%LOCALAPPDATA%\Android\Sdk\platform-tools\adb install app/build/outputs/apk/debug/app-debug.apk
```
## 3. Install via file transfer (no ADB)

1. Copy `app-debug.apk` to your phone (USB, Google Drive, Dropbox, etc.)
2. On the phone, tap the APK file and confirm the install
3. If blocked, enable **Install from unknown apps** for the file manager app in Settings
## 4. Verify
Open **AthleteOS** — the app seeds its database on first launch from `program.json` (bundled in assets). No sign-in or network required.
