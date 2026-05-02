package com.example.screenshame.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.screenshame.data.repository.UsageRepository
import java.util.concurrent.TimeUnit

class ShameNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val repository = UsageRepository(context)
        val usage = repository.getTodayUsage()
        val offenders = usage.filter { it.isOverLimit }

        if (offenders.isNotEmpty()) {
            val worst = offenders.maxByOrNull { it.usageMinutes - (it.limitMinutes ?: 0) }!!
            sendShameNotification(
                context = context,
                appName = worst.appName,
                usageMinutes = worst.usageMinutes,
                limitMinutes = worst.limitMinutes ?: 0
            )
        }

        return Result.success()
    }

    companion object {
        const val CHANNEL_ID = "screenshame_channel"
        const val WORK_NAME = "shame_check"

        fun schedule(context: Context) {
            createNotificationChannel(context)

            val request = PeriodicWorkRequestBuilder<ShameNotificationWorker>(
                30, TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }

        private fun createNotificationChannel(context: Context) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Shame Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifies you when you exceed your screen time limits"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        private fun sendShameNotification(
            context: Context,
            appName: String,
            usageMinutes: Int,
            limitMinutes: Int
        ) {
            val roasts = listOf(
                "You said $limitMinutes minutes. It has been $usageMinutes. You lied.",
                "$appName again. Of course it is.",
                "The limit was $limitMinutes minutes. The audacity.",
                "Your past self set that limit. Look what you did.",
                "$usageMinutes minutes on $appName. The pyramids were built in less time."
            )

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("[ALERT: SYSTEM SHAME]")
                .setContentText(roasts.random())
                .setStyle(NotificationCompat.BigTextStyle().bigText(roasts.random()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()

            val manager = context.getSystemService(NotificationManager::class.java)
            manager.notify(System.currentTimeMillis().toInt(), notification)
        }
    }
}