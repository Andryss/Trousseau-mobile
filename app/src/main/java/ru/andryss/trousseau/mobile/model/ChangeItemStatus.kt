package ru.andryss.trousseau.mobile.model

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.ItemStatus
import ru.andryss.trousseau.mobile.util.httpRequest

data class UpdateItemStatus(
    val status: ItemStatus
)

fun AppState.updateItemStatus(
    id: String,
    update: UpdateItemStatus,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send update item $id status $update request")
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
