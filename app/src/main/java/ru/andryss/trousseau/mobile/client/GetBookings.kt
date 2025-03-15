package ru.andryss.trousseau.mobile.client

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG

data class GetBookingsResponse(
    val items: List<PublicItemDto>,
)

fun AppState.getBookings(
    onSuccess: (items: List<PublicItemDto>) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get booked items request")
    httpRequest(
        "GET",
        "/public/items/bookings",
        callbackObj<GetBookingsResponse>(
            onSuccess = {
                Log.i(TAG, "Got ${it.items.size} items")
                onSuccess(it.items)
            },
            onError = onError
        )
    )
}
