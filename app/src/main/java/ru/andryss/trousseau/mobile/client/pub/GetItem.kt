package ru.andryss.trousseau.mobile.client.pub

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest

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
