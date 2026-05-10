package com.univeloued.rico.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.univeloued.rico.data.util.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ReminderReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("MEDICINE_NAME") ?: "Medicine"
        val intakeMethod = intent.getStringExtra("INTAKE_METHOD") ?: ""
        
        notificationHelper.showNotification(
            title = "Time for your medicine!",
            message = "Don't forget to take $medicineName $intakeMethod"
        )
    }
}
