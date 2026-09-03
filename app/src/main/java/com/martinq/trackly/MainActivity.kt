package com.martinq.trackly

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import com.martinq.trackly.databinding.ActivityMainBinding
import com.martinq.trackly.service.MusicNotificationService

/**
 * Minimal launcher activity.
 *
 * Its only responsibilities are:
 * 1. Show the Notification Access status (granted / not granted).
 * 2. Provide a button that opens the system Notification Access settings screen
 *    so the user can manually grant access.
 *
 * No media data is displayed here. UI will be expanded once the data pipeline is reliable.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOpenSettings.setOnClickListener {
            openNotificationAccessSettings()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPermissionStatus()
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    private fun refreshPermissionStatus() {
        val granted = isNotificationAccessGranted()
        binding.tvPermissionStatus.text = if (granted) {
            getString(R.string.permission_status_granted)
        } else {
            getString(R.string.permission_status_not_granted)
        }
        binding.btnOpenSettings.isEnabled = !granted
    }

    /**
     * Returns true when the system has bound to [MusicNotificationService], which means
     * Notification Access has been granted by the user.
     */
    private fun isNotificationAccessGranted(): Boolean {
        val cn = ComponentName(this, MusicNotificationService::class.java)
        val flat = Settings.Secure.getString(
            contentResolver,
            "enabled_notification_listeners"
        ) ?: return false
        return flat.split(":")
            .map { ComponentName.unflattenFromString(it) }
            .contains(cn)
    }

    /**
     * Opens the system Notification Access settings screen.
     */
    private fun openNotificationAccessSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        startActivity(intent)
    }
}
