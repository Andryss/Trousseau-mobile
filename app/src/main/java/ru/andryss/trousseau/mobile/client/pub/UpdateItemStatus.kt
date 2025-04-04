package ru.andryss.trousseau.mobile.client.pub

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.UpdateItemStatus
import ru.andryss.trousseau.mobile.client.updateItemStatusCommon

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