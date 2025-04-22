package ru.andryss.trousseau.mobile.client.auth

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ErrorObject
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.mapper

fun AppState.signIn(
    request: SignInRequest,
    onSuccess: (token: String) -> Unit,
    onError: (error: ErrorObject) -> Unit,
) {
    Log.i(TAG, "Send sign in request for ${request.username}")
    httpRequest(
        "POST",
        "/auth/signin",
        mapper.writeValueAsString(request),
        callbackObj<AuthResponse>(
            onSuccess = {
                Log.i(TAG, "Got sign in response token ${it.token.substring(0, 5)}...")
                onSuccess(it.token)
            },
            onError = onError
        )
    )
}