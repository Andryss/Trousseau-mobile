package ru.andryss.trousseau.mobile.client

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG

data class SearchInfo(
    val text: String,
)

data class SearchItemsResponse(
    val items: List<PublicItemDto>,
)

fun AppState.searchItems(
    search: SearchInfo,
    onSuccess: (items: List<PublicItemDto>) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send search items request")
    httpRequest(
        "POST",
        "/public/items:search",
        mapper.writeValueAsString(search),
        callbackObj<SearchItemsResponse>(
            onSuccess = {
                Log.i(TAG, "Found ${it.items.size} items")
                onSuccess(it.items)
            },
            onError = onError
        )
    )
}
