package ru.andryss.trousseau.mobile.client

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG

fun AppState.createSellerItem(
    onSuccess: (item: ItemDto) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send create seller item request")
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
