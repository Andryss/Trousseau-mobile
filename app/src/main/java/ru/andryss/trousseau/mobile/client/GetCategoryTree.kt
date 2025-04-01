package ru.andryss.trousseau.mobile.client

import android.util.Log
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG

data class CategoryTree(
    val root: CategoryNode
)

data class CategoryNode(
    val id: String,
    val name: String,
    val children: List<CategoryNode> = listOf()
) {
    companion object {
        val EMPTY = CategoryNode("", "")

        fun fromDto(dto: CategoryDto?) =
            dto?.let { CategoryNode(it.id, it.name) }
                ?: EMPTY
    }
}

fun AppState.getCategoryTree(
    onSuccess: (root: CategoryNode) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send get category tree request")
    httpRequest(
        "GET",
        "/public/categories/tree",
        callbackObj<CategoryTree>(
            onSuccess = {
                Log.i(TAG, "Got tree root ${it.root.id}")
                onSuccess(it.root)
            },
            onError = onError
        )
    )
}
