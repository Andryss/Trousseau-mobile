package ru.andryss.trousseau.mobile.client.pub

import android.util.Log
import com.fasterxml.jackson.annotation.JsonValue
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ErrorObject
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.ItemListResponse
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest
import ru.andryss.trousseau.mobile.client.mapper

enum class SortField(
    @get:JsonValue val value: String,
) {
    CREATED_AT("created_at"),
    COST("cost"),
}

enum class SortOrder(
    @get:JsonValue val value: String,
) {
    ASC("asc"),
    DESC("desc"),
}

data class SortInfo(
    val field: SortField,
    val order: SortOrder,
)

data class FilterInfo(
    val conditions: List<String>
)

data class SearchInfo(
    val text: String? = null,
    val sort: SortInfo,
    val filter: FilterInfo? = null,
    val page: PageInfo,
)

data class PageInfo(
    val size: Int,
    val token: String? = null,
)

fun AppState.searchItems(
    search: SearchInfo,
    onSuccess: (items: List<ItemDto>) -> Unit,
    onError: (error: ErrorObject) -> Unit,
) {
    Log.i(TAG, "Send search items request $search")
    httpRequest(
        "POST",
        "/public/items:search",
        mapper.writeValueAsString(search),
        callbackObj<ItemListResponse>(
            onSuccess = {
                Log.i(TAG, "Found ${it.items.size} items")
                onSuccess(it.items)
            },
            onError = onError
        ),
        authHeaders()
    )
}
