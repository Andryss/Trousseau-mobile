package ru.andryss.trousseau.mobile.client.pub

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ErrorObject
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.ItemListResponse
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest

fun AppState.getBookings(
    onSuccess: (items: List<ItemDto>) -> Unit,
    onError: (error: ErrorObject) -> Unit,
) {
    Log.i(TAG, "Send get booked items request")
    httpRequest(
        "GET",
        "/public/items/bookings",
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
