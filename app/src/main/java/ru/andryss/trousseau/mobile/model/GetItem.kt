package ru.andryss.trousseau.mobile.model

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.httpRequest

fun AppState.getItem(
    id: String,
    onSuccess: (item: ItemDto) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get item $id request")
    httpRequest(
        "GET",
        "/seller/items/$id",
        null,
        callbackObj<ItemDto>(
            onSuccess = {
                Log.i(TAG, "Got item ${it.id}")
                onSuccess(it)
            },
            onError = onError
        )
    )
}
