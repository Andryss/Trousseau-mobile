package ru.andryss.trousseau.mobile.client.pub.notifications

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ErrorObject
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.mapper
import ru.andryss.trousseau.mobile.client.noResponseCallbackObj

data class UpdateNotificationsTokenRequest(
    val token: String,
)

fun AppState.updateNotificationsToken(
    token: String,
    onSuccess: () -> Unit,
    onError: (error: ErrorObject) -> Unit,
) {
    Log.i(TAG, "Send update notifications token request")
    httpRequest(
        "POST",
        "/public/notifications/token",
        mapper.writeValueAsString(UpdateNotificationsTokenRequest(token)),
        noResponseCallbackObj(
            onSuccess = {
                Log.i(TAG, "Notification token updated")
                onSuccess()
            },
            onError = onError
        ),
        authHeaders()
    )
}