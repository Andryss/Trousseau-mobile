package ru.andryss.trousseau.mobile.client.pub.notifications

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ErrorObject
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest
import java.time.OffsetDateTime

data class NotificationListResponse(
    val notifications: List<NotificationDto>,
)

data class NotificationDto(
    val id: String,
    val title: String,
    val content: String,
    val links: List<String>,
    val timestamp: OffsetDateTime,
    val isRead: Boolean,
)

fun AppState.getNotifications(
    onSuccess: (notifications: List<NotificationDto>) -> Unit,
    onError: (error: ErrorObject) -> Unit,
) {
    Log.i(TAG, "Send get notifications request")
    httpRequest(
        "GET",
        "/public/notifications",
        callbackObj<NotificationListResponse>(
            onSuccess = {
                Log.i(TAG, "Got ${it.notifications.size} notifications")
                onSuccess(it.notifications)
            },
            onError = onError
        ),
        authHeaders()
    )
}