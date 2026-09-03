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
* Device tested: Not yet — requires physical device with Android API 26+
* Evidence:
  * `./gradlew build lint` exit code 0
  * `app-debug.apk` — 6.3 MB
* Commits: `app-001/setup-android-project`
* Files updated:
  * `claude-progress.md`
  * `feature_list.json`
  * `init.sh` (fixed markdown code fences)
  * All Android project files (new)

## Known Risks

* Media metadata varies between players and Android versions.
* MediaSession data may be incomplete or unavailable.
* Notification callbacks may repeat for the same track.
* Playback state changes must be distinguished from duplicate events.
* Notification Access requires manual user approval.
* OEM battery optimization may affect background services.
* Host Java 25 — if a future toolchain issue arises, install Java 17 JDK for toolchain resolution.

## Unresolved Issues

* Device testing for `app-001` not yet performed (requires physical device).
* `app-001` is partially passing (automated verification done; device verification pending).

## Next Best Step

* Perform device testing for `app-001` (install APK, grant Notification Access, verify no crash on return).
* If device not available: proceed to `service-001` — Implement NotificationListenerService Pipeline.
