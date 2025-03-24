package ru.andryss.trousseau.mobile.client

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG

fun AppState.getFeed(
    onSuccess: (items: List<ItemDto>) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get feed items request")
    httpRequest(
        "GET",
        "/public/items/feed",
        callbackObj<ItemListResponse>(
            onSuccess = {
                Log.i(TAG, "Got ${it.items.size} items")
                onSuccess(it.items)
            },
            onError = onError
        )
    )
}
