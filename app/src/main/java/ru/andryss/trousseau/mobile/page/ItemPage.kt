package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.UpdateItemStatus
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.pub.getItem
import ru.andryss.trousseau.mobile.client.pub.updateItemStatus
import ru.andryss.trousseau.mobile.util.ItemStatus
import ru.andryss.trousseau.mobile.widget.ActionButton
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.ItemInfo
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar

@Composable
fun ItemPage(state: AppState, itemId: String, callback: ItemPageCallback) {

    var getItemLoading by remember { mutableStateOf(false) }
    val bookItemLoading = remember { mutableStateOf(false) }
    val unbookItemLoading = remember { mutableStateOf(false) }

    var item by remember { mutableStateOf(ItemDto.EMPTY) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun updateStatus(
        loadingVar: MutableState<Boolean>,
        targetStatus: ItemStatus,
    ) {
        loadingVar.value = true
        state.updateItemStatus(
            item.id,
            UpdateItemStatus(status = targetStatus),
            onSuccess = {
                state.navigateBookingsPage()
                loadingVar.value = false
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert.value = true
                loadingVar.value = false
            }
        )
    }

    fun onBook() =
        updateStatus(bookItemLoading, ItemStatus.BOOKED)

    fun onUnbook() =
        updateStatus(unbookItemLoading, ItemStatus.PUBLISHED)

    LaunchedEffect(true) {
        getItemLoading = true
        state.getItem(
            itemId,
            onSuccess = { response ->
                item = response
                getItemLoading = false
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert.value = true
                getItemLoading = false
            }
        )
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Scaffold(
            topBar = {
                ReturnBackTopBar(
                    onReturn = {
                        when (callback) {
                            ItemPageCallback.HOME -> state.navigateHomePage()
                            ItemPageCallback.SEARCH -> state.navigateSearchPage()
                            ItemPageCallback.FAVOURITES -> state.navigateFavouritesPage()
                            ItemPageCallback.BOOKINGS -> state.navigateBookingsPage()
                        }
                    }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                ItemInfo(item)

                if (item.status == ItemStatus.PUBLISHED) {
                    ActionButton(
                        text = "Забронировать",
                        action = { onBook() }
                    )
                }

                if (item.status == ItemStatus.BOOKED) {
                    ActionButton(
                        text = "Снять бронирование",
                        action = { onUnbook() }
                    )
                }

                if (item.status == ItemStatus.ARCHIVED) {
                    ActionButton(
                        text = "Объявление в архиве",
                        action = { },
                        enabled = false
                    )
                }
            }
        }
    }
}

enum class ItemPageCallback(
    val path: String
) {
    HOME("home"),
    SEARCH("search"),
    FAVOURITES("favourites"),
    BOOKINGS("bookings");

    companion object {
        fun fromPath(path: String): ItemPageCallback {
            entries.forEach {
                if (it.path == path) return it
            }
            throw IllegalArgumentException("Unknown callback path $path")
        }
    }
}