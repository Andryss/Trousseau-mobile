package ru.andryss.trousseau.mobile.client.pub

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.ItemListResponse
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest

fun AppState.getFavourites(
    onSuccess: (items: List<ItemDto>) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get favourites request")
    httpRequest(
        "GET",
        "/public/items/favourites",
        callbackObj<ItemListResponse>(
            onSuccess = {
                Log.i(TAG, "Got ${it.items.size} items")
                onSuccess(it.items)
            },
            onError = onError
        ),
        authHeaders(),
    )
}
