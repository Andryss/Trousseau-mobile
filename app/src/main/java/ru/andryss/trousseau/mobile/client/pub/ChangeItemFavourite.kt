package ru.andryss.trousseau.mobile.client.pub

import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.mapper
import ru.andryss.trousseau.mobile.client.noResponseCallbackObj

data class UpdateFavourite(
    val isFavourite: Boolean
)

fun AppState.changeItemFavourite(
    itemId: String,
    isFavourite: Boolean,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit,
) {
    httpRequest(
        "POST",
        "/public/items/$itemId/favourite",
        mapper.writeValueAsString(UpdateFavourite(isFavourite)),
        noResponseCallbackObj(
            onSuccess = onSuccess,
            onError = onError
        )
    )
}