package ru.andryss.trousseau.mobile.model

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.httpRequest

data class UpdateItemInfo(
    val title: String?,
    val media: List<String>,
    val description: String?,
)

fun AppState.updateItem(
    id: String,
    update: UpdateItemInfo,
    onSuccess: (item: ItemDto) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send update item $id with $update request")
    httpRequest(
        "PUT",
        "/seller/items/$id",
        mapper.writeValueAsString(update),
        callbackObj<ItemDto>(
            onSuccess = {
                Log.i(TAG, "Got updated item ${it.id}")
                onSuccess(it)
            },
            onError = onError
        )
    )
}
