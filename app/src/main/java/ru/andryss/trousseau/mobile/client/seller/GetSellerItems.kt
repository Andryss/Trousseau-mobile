package ru.andryss.trousseau.mobile.client.seller

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.ItemListResponse
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest

fun AppState.getSellerItems(
    onSuccess: (items: List<ItemDto>) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get seller items request")
    httpRequest(
        "GET",
        "/seller/items",
        callbackObj<ItemListResponse>(
            onSuccess = {
                Log.i(TAG, "Got ${it.items.size} items")
                onSuccess(it.items)
            },
            onError = onError
        )
    )
}
