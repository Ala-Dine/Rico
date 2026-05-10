package com.univeloued.rico.data.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.univeloued.rico.data.receiver.ReminderReceiver
import com.univeloued.rico.domain.model.Reminder
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlarmHelper @Inject constructor(@ApplicationContext private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleReminder(reminder: Reminder) {
        if (!reminder.isActive) return

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("MEDICINE_NAME", reminder.medicineName)
            putExtra("INTAKE_METHOD", reminder.intakeMethod)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = parseTimeToCalendar(reminder.time) ?: return
        
        // If time has passed today, schedule for tomorrow
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    fun cancelReminder(reminder: Reminder) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.hashCode(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun parseTimeToCalendar(timeString: String): Calendar? {
        return try {
            val calendar = Calendar.getInstance()
            val format = java.text.SimpleDateFormat("h:mm a", Locale.US)
            val date = format.parse(timeString) ?: return null
            val timeCalendar = Calendar.getInstance().apply { time = date }
            
            calendar.set(Calendar.HOUR_OF_DAY, timeCalendar.get(Calendar.HOUR_OF_DAY))
            calendar.set(Calendar.MINUTE, timeCalendar.get(Calendar.MINUTE))
            calendar.set(Calendar.SECOND, 0)
            calendar
        } catch (e: Exception) {
            null
        }
    }
}
