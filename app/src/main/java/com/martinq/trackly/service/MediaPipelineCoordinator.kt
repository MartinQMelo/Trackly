package com.martinq.trackly.service

import android.content.Context
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * Coordinates the media event pipeline downstream of [MusicNotificationService].
 *
 * Current role (service-001):
 *  - Accept filtered media notification events.
 *  - Log the event with enough detail to verify filtering is working correctly.
 *  - Provide the wiring point for future pipeline stages:
 *      • media-001 — MediaSession metadata extraction
 *      • data-001  — Room persistence
 *      • data-002  — deduplication
 *
 * This coordinator intentionally does NOT perform metadata extraction or persistence yet.
 * Those responsibilities belong to future features and will replace the stub log calls below.
 */
class MediaPipelineCoordinator(private val context: Context) {

    /**
     * Called when a media notification is posted (new or updated).
     *
     * Pipeline stages that will be inserted here:
     *  1. (media-001) Extract MediaSession metadata via MediaController.
     *  2. (media-001) Fall back to Notification.extras when MediaSession is unavailable.
     *  3. (data-002)  Deduplicate against the last persisted event.
     *  4. (data-001)  Persist the normalized event to Room.
     */
    fun onMediaNotificationPosted(sbn: StatusBarNotification) {
        Log.d(TAG, "Pipeline entry — posted: pkg=${sbn.packageName} key=${sbn.key}")
        // TODO(media-001): extract metadata via MediaSessionManager / MediaController
        // TODO(data-002):  run deduplication
        // TODO(data-001):  persist to Room
    }

    /**
     * Called when a media notification is removed.
     *
     * Removal events may indicate the user stopped playback or dismissed the notification.
     * Handling will be refined in media-001 once playback state can be determined reliably.
     */
    fun onMediaNotificationRemoved(sbn: StatusBarNotification) {
        Log.d(TAG, "Pipeline entry — removed: pkg=${sbn.packageName} key=${sbn.key}")
        // TODO(media-001): determine whether removal implies a STOPPED state
    }

    companion object {
        private const val TAG = "MediaPipelineCoord"
    }
}
