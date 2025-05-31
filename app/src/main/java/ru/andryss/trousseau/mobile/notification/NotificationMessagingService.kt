package ru.andryss.trousseau.mobile.notification

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.MainActivity
import ru.andryss.trousseau.mobile.R
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.pub.notifications.updateNotificationsToken
import ru.andryss.trousseau.mobile.configureWith
import java.util.Random

const val NOTIFICATIONS_CHANNEL_ID = "notifications_channel"
const val NOTIFICATIONS_CHANNEL_NAME = "Notifications"

fun Activity.configureNotificationWorker() {
    Log.i(TAG, "Configuring notification worker")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (checkSelfPermission(POST_NOTIFICATIONS) != PERMISSION_GRANTED) {
            requestPermissions(arrayOf(POST_NOTIFICATIONS), 1)
        }
    }

    val channel = NotificationChannel(
        NOTIFICATIONS_CHANNEL_ID,
        NOTIFICATIONS_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_DEFAULT
    )
    getNotificationManager().createNotificationChannel(channel)

    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.w(TAG, "Fetching FCM registration token failed", task.exception)
            return@addOnCompleteListener
        }
        sendTokenToServer(task.result)
    }

    Log.i(TAG, "Notification worker configured")
}

class NotificationMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notificationInfo = remoteMessage.notification
        Log.i(TAG, "Received notification $notificationInfo")
        notificationInfo ?: return

        val intent = PendingIntent.getActivity(
            applicationContext,
            Random().nextInt(),
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, NOTIFICATIONS_CHANNEL_ID)
            .setContentTitle(notificationInfo.title)
            .setContentText(notificationInfo.body)
            .setSmallIcon(R.mipmap.trousseau_round)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(intent)
            .build()

        val notificationManager = applicationContext.getNotificationManager()

        notificationManager.notify(Random().nextInt(), notification)
    }

    override fun onNewToken(token: String) {
        sendTokenToServer(token)
    }
}

private fun Context.getNotificationManager() =
    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

private fun ContextWrapper.sendTokenToServer(token: String) {
    val state = AppState()
    state.configureWith(applicationContext)

    state.updateNotificationsToken(
        token,
        onSuccess = {
            Log.i(TAG, "Updated notifications token successfully")
        },
        onError = { error ->
            Log.e(TAG, "Got error while updating notifications token: $error")
        }
    )
}