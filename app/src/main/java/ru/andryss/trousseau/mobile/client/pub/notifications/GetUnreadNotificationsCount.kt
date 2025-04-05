package ru.andryss.trousseau.mobile.client.pub.notifications

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest

data class NotificationCountResponse(
    val count: Int
)

fun AppState.getUnreadNotificationsCount(
    onSuccess: (count: Int) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get unread notifications request")
    httpRequest(
        "GET",
        "/public/notifications/unread/count",
        callbackObj<NotificationCountResponse>(
            onSuccess = {
                Log.i(TAG, "Got unread notifications count ${it.count}")
                onSuccess(it.count)
            },
            onError = onError
        )
    )
}