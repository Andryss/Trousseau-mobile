package ru.andryss.trousseau.mobile.client.seller

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.AuthorDto
import ru.andryss.trousseau.mobile.client.ErrorObject
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest
import java.time.OffsetDateTime

data class BookingDto(
    val author: AuthorDto,
    val bookedAt: OffsetDateTime,
) {
    companion object {
        val EMPTY = BookingDto(AuthorDto.EMPTY, OffsetDateTime.MIN)
    }
}

fun AppState.getItemBookingInfo(
    id: String,
    onSuccess: (item: BookingDto) -> Unit,
    onError: (error: ErrorObject) -> Unit,
) {
    Log.i(TAG, "Send get item $id booking info request")
    httpRequest(
        "GET",
        "/seller/items/$id/booking",
        callbackObj<BookingDto>(
            onSuccess = {
                Log.i(TAG, "Got booking $it")
                onSuccess(it)
            },
            onError = onError
        ),
        authHeaders(),
    )
}
