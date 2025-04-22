package ru.andryss.trousseau.mobile.client.pub.notifications

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ErrorObject
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.noResponseCallbackObj

fun AppState.markNotificationRead(
    notificationId: String,
    onSuccess: () -> Unit,
    onError: (error: ErrorObject) -> Unit,
) {
    Log.i(TAG, "Send mark notification read request")
    httpRequest(
        "POST",
        "/public/notifications/$notificationId/read",
        "",
        noResponseCallbackObj(
            onSuccess = {
                Log.i(TAG, "Notification $notificationId marked read")
                onSuccess()
            },
            onError = onError
        ),
        authHeaders()
    )
}