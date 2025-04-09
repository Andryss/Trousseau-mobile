package ru.andryss.trousseau.mobile.client.pub.notifications

import android.util.Log
import com.fasterxml.jackson.module.kotlin.readValue
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.RESPONSE_PARSE_ERROR_MESSAGE
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.mapper

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

fun AppState.getUnreadNotificationsCount(): Int {
    Log.i(TAG, "Send get unread notifications request")
    val response = httpRequest(
        "GET",
        "/public/notifications/unread/count",
        null
    )
    val code = response.code
    val body = response.body?.string()
    Log.i(TAG, "Got response $code $body")
    if (code != 200 || body == null) {
        throw Exception(RESPONSE_PARSE_ERROR_MESSAGE)
    }
    val result = mapper.readValue<NotificationCountResponse>(body)
    return result.count
}