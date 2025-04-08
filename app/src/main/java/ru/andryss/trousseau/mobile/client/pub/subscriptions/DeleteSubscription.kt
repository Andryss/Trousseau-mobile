package ru.andryss.trousseau.mobile.client.pub.subscriptions

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.noResponseCallbackObj

fun AppState.deleteSubscription(
    id: String,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send delete subscription $id request")
    httpRequest(
        "DELETE",
        "/public/subscriptions/$id",
        noResponseCallbackObj(
            onSuccess = {
                Log.i(TAG, "Deleted $id subscription")
                onSuccess()
            },
            onError = onError
        )
    )
}
