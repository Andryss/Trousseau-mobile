package ru.andryss.trousseau.mobile.client.pub.subscriptions

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.mapper

data class SubscriptionInfoRequest(
    val name: String,
    val data: SubscriptionData
)

fun AppState.createSubscription(
    request: SubscriptionInfoRequest,
    onSuccess: (subscription: SubscriptionDto) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send create subscription request $request")
    httpRequest(
        "POST",
        "/public/subscriptions",
        mapper.writeValueAsString(request),
        callbackObj<SubscriptionDto>(
            onSuccess = {
                Log.i(TAG, "Created ${it.id} subscription")
                onSuccess(it)
            },
            onError = onError
        )
    )
}
