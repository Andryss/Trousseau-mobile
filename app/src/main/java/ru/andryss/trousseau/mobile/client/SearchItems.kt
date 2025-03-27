package ru.andryss.trousseau.mobile.client

import android.util.Log
import com.fasterxml.jackson.annotation.JsonValue
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG

enum class SortField(
    @get:JsonValue val value: String,
) {
    CREATED_AT("created_at"),
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
    onError: (error: String) -> Unit,
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
        )
    )
}
