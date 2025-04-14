package ru.andryss.trousseau.mobile.client

import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.util.ItemStatus

data class UpdateItemStatus(
    val status: ItemStatus
)

fun AppState.updateItemStatusCommon(
    url: String,
    update: UpdateItemStatus,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit,
) {
    httpRequest(
        "PUT",
        url,
        mapper.writeValueAsString(update),
        noResponseCallbackObj(
            onSuccess = onSuccess,
            onError = onError
        ),
        authHeaders(),
    )
}
