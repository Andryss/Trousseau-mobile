package ru.andryss.trousseau.mobile.client

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG

fun AppState.getItem(
    id: String,
    onSuccess: (item: ItemDto) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get item request")
    httpRequest(
        "GET",
        "/public/items/$id",
        callbackObj<ItemDto>(
            onSuccess = {
                Log.i(TAG, "Got item $it")
                onSuccess(it)
            },
            onError = onError
        )
    )
}
