package com.example.athleteos.features.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.example.athleteos.R
import java.util.Calendar
import java.util.concurrent.TimeUnit

class TrainingReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString("title") ?: "AthleteOS"
        val message = inputData.getString("message") ?: "Time to train."

        createNotificationChannel()
        showNotification(title, message)

        return Result.success()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Training Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Daily training reminders"
            }
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification(title: String, message: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        const val CHANNEL_ID = "training_reminders"

        fun scheduleReminders(context: Context) {
            scheduleReminder(context, 18, 0, "evening_reminder", "Today's Training", "Don't forget your workout today!")
            scheduleReminder(context, 21, 0, "night_reminder", "Workout Not Started", "You haven't started today's workout yet.")
        }

        private fun scheduleReminder(context: Context, hour: Int, minute: Int, tag: String, title: String, message: String) {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                if (before(now)) {
                    add(Calendar.DAY_OF_YEAR, 1)
                }
            }

            val delay = target.timeInMillis - now.timeInMillis

            val data = workDataOf(
                "title" to title,
                "message" to message
            )

            val request = PeriodicWorkRequestBuilder<TrainingReminderWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .addTag(tag)
                .build()

            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(tag, ExistingPeriodicWorkPolicy.UPDATE, request)
        }

        fun cancelReminders(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag("evening_reminder")
            WorkManager.getInstance(context).cancelAllWorkByTag("night_reminder")
        }
    }
}
