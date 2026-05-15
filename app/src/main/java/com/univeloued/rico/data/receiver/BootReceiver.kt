package com.univeloued.rico.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.univeloued.rico.data.local.dao.ReminderDao
import com.univeloued.rico.data.mapper.toDomain
import com.univeloued.rico.data.util.AlarmHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var reminderDao: ReminderDao

    @Inject
    lateinit var alarmHelper: AlarmHelper

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val reminders = reminderDao.getAllRemindersSync()
                reminders.forEach { entity ->
                    val domain = entity.toDomain()
                    if (domain.isActive) {
                        alarmHelper.scheduleReminder(domain)
                    }
                }
            }
        }
    }
}
