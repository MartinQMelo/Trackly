# AGENTS.md

This repository is designed for long-running coding-agent work on a native Android application using Kotlin.

The goal is to build a reliable background service that detects currently playing media, extracts metadata from Android's media framework, deduplicates repeated events, and persists meaningful playback events locally.

## Project Architecture

The expected pipeline is:

```text
NotificationListenerService
        |
        v
MediaSessionManager / MediaController
        |
        +----> Primary metadata source
        |
        +----> Notification.extras fallback
        |
        v
Normalized Media Event
        |
        v
Deduplication
        |
        v
Room Database
```

* `NotificationListenerService` observes media-related notification events.
* `MediaSessionManager` / `MediaController` is the primary source of media metadata and playback state.
* `Notification.extras` is the fallback when reliable MediaSession metadata cannot be obtained.
* Deduplication happens before persistence.
* Room handles local persistence.
* UI should remain minimal until the data pipeline is reliable.

Do not implement the application as a notification text scraper.

## Startup Workflow

Before writing or modifying code:

1. Confirm the working directory with `pwd`.
2. Read `claude-progress.md`.
3. Read `feature_list.json`.
4. Inspect recent commits:

```bash
git log --oneline -5
```

5. Inspect the working tree:

```bash
git status --short
```

6. Run:

```bash
./init.sh
```

7. Confirm:

```bash
./gradlew build lint
```

If baseline verification fails, determine whether the failure was pre-existing or caused by the current work. Document blockers in `claude-progress.md`.

## Working Rules

### One Feature at a Time

Work on one unfinished feature at a time.

### MediaSession

Use `MediaSessionManager` / `MediaController` whenever possible to obtain:

* title
* artist
* playback state
* package name

Use notification extras only when MediaSession metadata is unavailable or incomplete.

Do not assume all media applications expose the same metadata.

### NotificationListenerService

`NotificationListenerService` is an observation layer, not the authoritative media data source.

Notifications may be updated repeatedly for the same track because of playback progress, state changes, metadata changes, or player-specific behavior.

Every observed event must pass through normalization and deduplication before persistence.

### Deduplication

Never insert directly into Room from a raw notification callback.

The minimum comparison identity is:

```text
title + artist + packageName + playbackState
```

Required behavior:

```text
A + PLAYING
A + PLAYING   -> ignore

A + PAUSED
A + PAUSED    -> ignore

A + PLAYING   -> save

A + PLAYING
B + PLAYING   -> save
```

A different package is also a new event.

## Data Model

The persisted event must contain:

* `id`
* `title`
* `artist`
* `packageName`
* `playbackState`
* `timestamp`

Use nullable fields when metadata is genuinely unavailable.

Do not invent metadata.

## Android Permissions

The application must declare the notification listener service correctly and provide a minimal UI for opening the system Notification Access settings.

Notification Access must be granted manually by the user.

## Verification Rules

Do not change verification requirements to make a feature pass.

A feature is passing only when:

1. implementation is complete;
2. automated verification has run successfully;
3. required manual/device verification has been performed;
4. evidence is recorded.

Standard verification:

```bash
./gradlew build lint
```

Do not claim verification passed unless it actually ran successfully.

## Required Artifacts

* `feature_list.json`: feature state and verification.
* `claude-progress.md`: session state, evidence, blockers and risks.
* `init.sh`: standard initialization and verification path.

## Definition of Done

A feature is done only when:

* the target behavior is implemented in Kotlin;
* the defined architecture is followed;
* automated verification passes;
* required manual/device verification passes;
* evidence is recorded;
* known regressions are documented.

## Device Testing

The media pipeline must eventually be tested on a physical Android device.

Verify:

1. debug APK installation;
2. Notification Access permission;
3. media playback;
4. title;
5. artist when available;
6. package name;
7. `PLAYING`;
8. `PAUSED`;
9. resume as a new `PLAYING` event;
10. track changes;
11. absence of duplicates;
12. unrelated notifications being ignored;
13. behavior with another media player when available.

Record the device, Android version, media players and limitations in `claude-progress.md`.

## End Of Session

Before ending a session:

1. Update `claude-progress.md`.
2. Update `feature_list.json`.
3. Record verification commands and results.
4. Record unresolved risks and blockers.
5. Review `git status --short`.
6. Create a descriptive commit only when the repository is in a safe state.

Never mark a feature as `passing` without evidence.

## Known Risks

* Media metadata varies between players and Android versions.
* MediaSession data may be incomplete or unavailable.
* Notification callbacks may repeat.
* Playback state changes must be distinguished from duplicate events.
* Notification Access requires manual user approval.
* OEM battery optimization may affect background services.
* Behavior may vary across Android versions and manufacturers.
