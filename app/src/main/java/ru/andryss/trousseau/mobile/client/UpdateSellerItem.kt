package ru.andryss.trousseau.mobile.client

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG

data class UpdateItemInfo(
    val title: String?,
    val media: List<String>,
    val description: String?,
)

fun AppState.updateSellerItem(
    id: String,
    update: UpdateItemInfo,
    onSuccess: (item: ItemDto) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send update seller item $id with $update request")
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
