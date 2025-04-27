package ru.andryss.trousseau.mobile.client.auth

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ErrorObject
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest

fun AppState.getProfileInfo(
    onSuccess: (response: ProfileDto) -> Unit,
    onError: (error: ErrorObject) -> Unit,
) {
    Log.i(TAG, "Send get profile info request")
    httpRequest(
        "GET",
        "/auth/profile",
        callbackObj<ProfileDto>(
            onSuccess = {
                Log.i(TAG, "Got profile info $it")
                onSuccess(it)
            },
            onError = onError
        ),
        authHeaders(),
    )
}

fun AppState.updateProfileInfo() {
    Log.i(TAG, "Updating profile info")
    getProfileInfo(
        onSuccess = { result ->
            cache.profileCache.profile.value = result
        },
        onError = { error ->
            Log.e(TAG, "Got error while fetching profile info: $error")
        }
    )
}