package ru.andryss.trousseau.mobile.client.auth

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.mapper

fun AppState.signUp(
    request: SignUpRequest,
    onSuccess: (token: String) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send sign up request for " +
            "${request.username}, ${request.contacts}, ${request.room}")
    httpRequest(
        "POST",
        "/auth/signup",
        mapper.writeValueAsString(request),
        callbackObj<AuthResponse>(
            onSuccess = {
                Log.i(TAG, "Got sign up response token ${it.token.substring(0, 5)}...")
                onSuccess(it.token)
            },
            onError = onError
        )
    )
}