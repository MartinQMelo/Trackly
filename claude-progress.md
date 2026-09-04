# Progress Log

## Current Verified State

* Repository root: `/home/martin/Projects/MusicRewind`
* Standard initialization path: `./init.sh`
* Standard verification path: `./gradlew build lint`
* Standard device installation: `RUN_START_COMMAND=1 ./init.sh`
* Current highest-priority unfinished feature: `service-001` — Implement NotificationListenerService Pipeline
* Current blocker: None

## Session Log

### Session 001

* Date: 2026-09-02
* Goal: Implement `app-001` — Setup Android Project & Permissions
* Active feature: `app-001`
* Completed: Android project scaffolded from scratch; `./gradlew build lint` passes.

#### What was built

* Android project created manually (no Android Studio):
  * `settings.gradle.kts`, `build.gradle.kts`, `app/build.gradle.kts`
  * `gradle/libs.versions.toml` — AGP 9.4.0, KSP 2.3.11, Gradle 9.6.0
  * `gradle/wrapper/gradle-wrapper.{properties,jar}`
  * `gradlew` (shell script)
  * `local.properties` — points to `~/Android` SDK (not committed)
  * `app/src/main/AndroidManifest.xml` — declares `MusicNotificationService` NLS with correct permission and intent-filter
  * `MainActivity.kt` — reads `enabled_notification_listeners` from `Settings.Secure`, shows status and a button that opens `ACTION_NOTIFICATION_LISTENER_SETTINGS`
  * `MusicNotificationService.kt` — skeleton `NotificationListenerService`; media logic deferred to `service-001`
  * `activity_main.xml`, `strings.xml`, `themes.xml`, `colors.xml`, adaptive icon drawables

* SDK installed: `~/Android/cmdline-tools/latest`, `platforms;android-35`, `build-tools;34.0.0`

#### Compatibility notes

* Host Java version: OpenJDK 25.0.4.1 (Red Hat)
* Gradle 9.6.0 is required by AGP 9.4.0 (minimum)
* Kotlin 2.3+ is required for Java 25 compatibility; AGP 9.0 provides built-in Kotlin — `org.jetbrains.kotlin.android` plugin is NOT applied
* `compileSdk = 35` required by `androidx.core:core-ktx:1.16.0` and `androidx.activity:activity-ktx:1.10.1`
* `MEDIA_CONTENT_CONTROL` is a system-only permission — removed (not needed for NLS-based MediaSession access)
* `jvmToolchain(17)` cannot be used because no Java 17 installation is present; `compileOptions` source/target set to 17 bytecode instead

* Verification commands:
  ```
  ./gradlew build lint
  ```
* Verification results: **BUILD SUCCESSFUL** — 95 actionable tasks, lint passed (no errors), debug APK produced at `app/build/outputs/apk/debug/app-debug.apk`
* Evidence:
  * `./gradlew build lint` exit code 0
  * `app-debug.apk` — 6.3 MB
* Commits: `app-001/setup-android-project`
* Files updated:
  * `claude-progress.md`
  * `feature_list.json`
  * `init.sh` (fixed markdown code fences)
  * All Android project files (new)

---

### Session 002

* Date: 2026-09-04
* Goal: Record `app-001` device verification; implement `service-001` — Implement NotificationListenerService Pipeline
* Active feature: `service-001`

#### app-001 Device Verification (closing)

* Device: Poco X7 Pro
* Android version: Not recorded (user-reported)
* Steps performed:
  1. debug APK installed on device
  2. Notification Access manually granted through Android settings
  3. App returned to without crashing
* Outcome: All `app-001` verification criteria satisfied.
* `app-001` status: **passing**

#### service-001 — What was built

* `MusicNotificationService.kt` — replaced skeleton with full media notification filtering:
  * `onNotificationPosted` / `onNotificationRemoved` — null-safe, routes filtered events to `MediaPipelineCoordinator`
  * `isMediaNotification()` — classification logic:
    * `CATEGORY_TRANSPORT` → always pass (explicit media transport control)
    * `CATEGORY_SERVICE` + `android.mediaSession` extras key present → pass (e.g. Spotify service notifications with MediaSession)
    * All other categories (MESSAGE, EMAIL, SOCIAL, …) → reject; logged as ignored
  * Non-media packages (WhatsApp, Gmail, etc.) are silently discarded; only a `Log.v` is emitted.

* `MediaPipelineCoordinator.kt` — new class; entry point for downstream pipeline stages:
  * `onMediaNotificationPosted(sbn)` — logs event, contains TODO markers for media-001 (MediaSession extraction), data-002 (dedup), data-001 (Room persistence)
  * `onMediaNotificationRemoved(sbn)` — logs event, TODO for media-001 STOPPED state handling

#### Automated verification

```
./gradlew build lint
```

Result: **BUILD SUCCESSFUL** — 95 actionable tasks, 23 executed, lint passed (no errors)
Exit code: 0

#### Device verification (pending)

Install the updated APK on the Poco X7 Pro and verify via logcat:

```
adb logcat -s MusicNotificationSvc:V MediaPipelineCoord:D
```

Expected behavior:
1. Play a track in a media app (e.g. Spotify, YouTube Music) → logcat shows:
   - `MusicNotificationSvc: Media notification posted from <pkg>`
   - `MediaPipelineCoord: Pipeline entry — posted: pkg=<pkg> key=<key>`
2. Receive a WhatsApp or Gmail notification → logcat shows:
   - `MusicNotificationSvc: Ignored non-media notification from <pkg>`
   - No `MediaPipelineCoord` entry logged.
3. Repeated notification updates from media player → logcat shows repeated pipeline entries (dedup not yet active; that's data-002).

* Commits: `service-001/notification-listener-pipeline`
* Files updated:
  * `claude-progress.md`
  * `feature_list.json`
  * `app/src/main/java/com/martinq/trackly/service/MusicNotificationService.kt`
  * `app/src/main/java/com/martinq/trackly/service/MediaPipelineCoordinator.kt` (new)

## Known Risks

* Media metadata varies between players and Android versions.
* MediaSession data may be incomplete or unavailable.
* Notification callbacks may repeat for the same track.
* Playback state changes must be distinguished from duplicate events.
* Notification Access requires manual user approval.
* OEM battery optimization may affect background services.
* Host Java 25 — if a future toolchain issue arises, install Java 17 JDK for toolchain resolution.
* Some media apps (e.g. Spotify) use `CATEGORY_SERVICE`; the MediaSession extras key heuristic may not cover all players — revisit in media-001 if gaps are found.

## Unresolved Issues

* `service-001` device verification not yet performed (requires updated APK on Poco X7 Pro and logcat observation).
* `service-001` is partially passing (automated verification done; device verification pending).

## Next Best Step

* Install the updated APK (`RUN_START_COMMAND=1 ./init.sh`) on the Poco X7 Pro.
* Use logcat to verify media notifications are captured and non-media notifications are ignored.
* Record results and mark `service-001` as passing (or document gaps).
* Proceed to `media-001` — Extract Media Metadata via MediaSession.
