package ru.andryss.trousseau.mobile.client.auth

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.noResponseCallbackObj

fun AppState.signOut(
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send sign out request")
    httpRequest(
        "POST",
        "/auth/signout",
        "",
        noResponseCallbackObj(
            onSuccess = {
                Log.i(TAG, "Successfully signed out")
                onSuccess()
            },
            onError = onError
        ),
        authHeaders()
    )
}