package ru.andryss.trousseau.mobile.model

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.httpRequest

fun AppState.createItem(
    onSuccess: (item: ItemDto) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send create item request")
    httpRequest(
        "POST",
        "/seller/items",
        "{}",
        callbackObj<ItemDto>(
            onSuccess = {
                Log.i(TAG, "Created new item ${it.id}")
                onSuccess(it)
            },
            onError = onError
        )
    )
}
