package ru.andryss.trousseau.mobile.notification

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.MainActivity
import ru.andryss.trousseau.mobile.R
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.pub.notifications.getUnreadNotificationsCount
import ru.andryss.trousseau.mobile.configureWith
import java.util.Random
import java.util.concurrent.TimeUnit

const val NOTIFICATION_TAG = "trousseau-notification-worker"

const val UNIQUE_WORKER_NAME = "notification_worker"

const val NOTIFICATIONS_CHANNEL_ID = "notifications_channel"
const val NOTIFICATIONS_CHANNEL_NAME = "Notifications"
const val NOTIFICATION_TITLE = "Новые уведомления"
const val NOTIFICATION_TEXT = "У вас есть непрочитанные уведомления: %s"

fun Activity.configureNotificationWorker() {
    Log.i(TAG, "Configuring notification worker")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (checkSelfPermission(POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            requestPermissions(arrayOf(POST_NOTIFICATIONS), 1)
        }
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            NOTIFICATIONS_CHANNEL_ID,
            NOTIFICATIONS_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        getNotificationManager().createNotificationChannel(channel)
    }

    val workRequest = PeriodicWorkRequestBuilder<NotificationWorker>(15, TimeUnit.MINUTES)
        .setInitialDelay(1, TimeUnit.MINUTES)
        .build()

    WorkManager.getInstance(this).enqueueUniquePeriodicWork(
        UNIQUE_WORKER_NAME,
        ExistingPeriodicWorkPolicy.UPDATE,
        workRequest
    )

    Log.i(TAG, "Notification worker configured")
}

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        Log.i(NOTIFICATION_TAG, "Notification worker started")

        if (ContextCompat.checkSelfPermission(applicationContext, POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            Log.i(NOTIFICATION_TAG, "Notification worker finished cause permission denied")
            return Result.failure()
        }

        try {
            val unreadCount = fetchUnreadNotificationsCount()
            if (unreadCount > 0) {
                showNotification(unreadCount)
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e(NOTIFICATION_TAG, "Notification worker failed with exception", e)
            return Result.retry()
        } finally {
            Log.i(NOTIFICATION_TAG, "Notification worker finished")
        }
    }

    private fun fetchUnreadNotificationsCount(): Int {
        val state = AppState()
        state.configureWith(applicationContext)

        return state.getUnreadNotificationsCount()
    }

    private fun showNotification(unreadCount: Int) {
        val notificationManager = applicationContext.getNotificationManager()

        val intent = PendingIntent.getActivity(
            applicationContext,
            Random().nextInt(),
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATIONS_CHANNEL_ID)
            .setContentTitle(NOTIFICATION_TITLE)
            .setContentText(NOTIFICATION_TEXT.format(unreadCount))
            .setSmallIcon(R.mipmap.trousseau_round)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
            .build()

        notificationManager.notify(Random().nextInt(), notification)
    }
}

private fun Context.getNotificationManager() =
    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager