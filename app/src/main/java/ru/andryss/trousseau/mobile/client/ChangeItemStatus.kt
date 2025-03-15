package ru.andryss.trousseau.mobile.client

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.ItemStatus

data class UpdateItemStatus(
    val status: ItemStatus
)

fun AppState.updateSellerItemStatus(
    id: String,
    update: UpdateItemStatus,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send update seller item $id status $update request")
    updateItemStatusCommon(
        "/seller/items/$id/status",
        update, onSuccess, onError
    )
}

fun AppState.updateItemStatus(
    id: String,
    update: UpdateItemStatus,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send update item $id status $update request")
    updateItemStatusCommon(
        "/public/items/$id/status",
        update, onSuccess, onError
    )
}

private fun AppState.updateItemStatusCommon(
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
        )
    )
}
