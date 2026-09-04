package com.martinq.trackly.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * NotificationListenerService — the observation layer of the media pipeline.
 *
 * Responsibilities (service-001):
 *  1. Receive all notification events from the system.
 *  2. Filter: pass only media-category notifications into the evaluation pipeline.
 *  3. Hand off filtered events to [MediaPipelineCoordinator] for further processing.
 *
 * What this class does NOT do:
 *  - Extract MediaSession metadata (media-001).
 *  - Persist to Room (data-001).
 *  - Deduplicate (data-002).
 */
class MusicNotificationService : NotificationListenerService() {

    private val coordinator: MediaPipelineCoordinator by lazy {
        MediaPipelineCoordinator(applicationContext)
    }

    // -------------------------------------------------------------------------
    // Service lifecycle
    // -------------------------------------------------------------------------

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.i(TAG, "NotificationListenerService connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.i(TAG, "NotificationListenerService disconnected")
    }

    // -------------------------------------------------------------------------
    // Notification events
    // -------------------------------------------------------------------------

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        if (!isMediaNotification(sbn)) {
            Log.v(TAG, "Ignored non-media notification from ${sbn.packageName}")
            return
        }
        Log.d(TAG, "Media notification posted from ${sbn.packageName}")
        coordinator.onMediaNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn ?: return
        if (!isMediaNotification(sbn)) return
        Log.d(TAG, "Media notification removed from ${sbn.packageName}")
        coordinator.onMediaNotificationRemoved(sbn)
    }

    // -------------------------------------------------------------------------
    // Media notification classification
    // -------------------------------------------------------------------------

    /**
     * Returns true when [sbn] is a media notification that should enter the pipeline.
     *
     * Classification criteria (evaluated in order, first match wins):
     *  1. [Notification.CATEGORY_TRANSPORT] — explicit media transport control category.
     *  2. [Notification.CATEGORY_SERVICE]   — some players (e.g. Spotify) use service category
     *     with a MediaSession attached; included to avoid false negatives.
     *  3. Notification carries a non-null MediaStyle media session token
     *     (detected via extras key EXTRA_MEDIA_SESSION).
     *
     * Non-media categories (CATEGORY_MESSAGE, CATEGORY_EMAIL, CATEGORY_SOCIAL, etc.)
     * are rejected, making WhatsApp / Gmail / social-app notifications invisible to the pipeline.
     */
    private fun isMediaNotification(sbn: StatusBarNotification): Boolean {
        val notification = sbn.notification ?: return false

        // Criterion 1 & 2: category-based fast path
        when (notification.category) {
            Notification.CATEGORY_TRANSPORT -> return true
            Notification.CATEGORY_SERVICE   -> { /* fall through to extras check */ }
            else                            -> return false
        }

        // Criterion 3: CATEGORY_SERVICE notifications pass only when a MediaSession
        // token is present in the extras, indicating this is a media playback service.
        val extras = notification.extras ?: return false
        val hasMediaSession = extras.containsKey("android.mediaSession")
        if (hasMediaSession) {
            return true
        }

        Log.v(TAG, "CATEGORY_SERVICE notification from ${sbn.packageName} has no MediaSession — ignored")
        return false
    }

    // -------------------------------------------------------------------------
    // Companion
    // -------------------------------------------------------------------------

    companion object {
        private const val TAG = "MusicNotificationSvc"
    }
}
