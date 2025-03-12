package ru.andryss.trousseau.mobile.client

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG

fun AppState.getSellerItem(
    id: String,
    onSuccess: (item: ItemDto) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get seller item $id request")
    httpRequest(
        "GET",
        "/seller/items/$id",
        callbackObj<ItemDto>(
            onSuccess = {
                Log.i(TAG, "Got item ${it.id}")
                onSuccess(it)
            },
            onError = onError
        )
    )
}
