package ru.andryss.trousseau.mobile.client.seller

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest

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
                Log.i(TAG, "Got item $it")
                onSuccess(it)
            },
            onError = onError
        ),
        authHeaders(),
    )
}
