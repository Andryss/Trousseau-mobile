package ru.andryss.trousseau.mobile.client.pub.subscriptions

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest

data class SubscriptionListResponse(
    val subscriptions: List<SubscriptionDto>
)

fun AppState.getSubscriptions(
    onSuccess: (subscriptions: List<SubscriptionDto>) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get subscriptions request")
    httpRequest(
        "GET",
        "/public/subscriptions",
        callbackObj<SubscriptionListResponse>(
            onSuccess = {
                Log.i(TAG, "Got ${it.subscriptions.size} subscriptions")
                onSuccess(it.subscriptions)
            },
            onError = onError
        )
    )
}
