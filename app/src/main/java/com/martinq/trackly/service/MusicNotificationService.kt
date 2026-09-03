package com.martinq.trackly.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

/**
 * NotificationListenerService skeleton for feature app-001.
 *
 * This class is the observation layer of the media pipeline. Its role is strictly
 * to receive system notification events and pass relevant ones into the pipeline.
 *
 * Implementation notes for future features:
 * - Media metadata extraction (MediaSession/MediaController) belongs to feature media-001.
 * - Room persistence belongs to feature data-001.
 * - Deduplication belongs to feature data-002.
 *
 * For now, this file only satisfies the manifest declaration requirement and ensures
 * the service lifecycle is functional (onListenerConnected / onListenerDisconnected).
 */
class MusicNotificationService : NotificationListenerService() {

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "NotificationListenerService connected")
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        Log.d(TAG, "NotificationListenerService disconnected")
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // Placeholder: media evaluation pipeline will be wired here in service-001.
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Placeholder: track removal events if needed in future features.
    }

    companion object {
        private const val TAG = "MusicNotificationSvc"
    }
}
