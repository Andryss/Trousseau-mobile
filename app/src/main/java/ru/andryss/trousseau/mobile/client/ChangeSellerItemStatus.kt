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
    httpRequest(
        "PUT",
        "/seller/items/$id/status",
        mapper.writeValueAsString(update),
        noResponseCallbackObj(
            onSuccess = onSuccess,
            onError = onError
        )
    )
}
