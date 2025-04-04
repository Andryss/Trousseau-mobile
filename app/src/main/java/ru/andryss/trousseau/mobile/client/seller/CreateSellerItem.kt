package ru.andryss.trousseau.mobile.client.seller

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest

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
