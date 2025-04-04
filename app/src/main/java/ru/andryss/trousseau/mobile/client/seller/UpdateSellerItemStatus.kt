package ru.andryss.trousseau.mobile.client.seller

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.UpdateItemStatus
import ru.andryss.trousseau.mobile.client.updateItemStatusCommon


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