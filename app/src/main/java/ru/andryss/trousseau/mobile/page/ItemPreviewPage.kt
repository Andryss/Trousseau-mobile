package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.seller.getSellerItem
import ru.andryss.trousseau.mobile.widget.ActionButton
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.ItemInfo
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar

@Composable
fun ItemPreviewPage(state: AppState, itemId: String) {

    var getItemLoading by remember { mutableStateOf(false) }

    var item by remember { mutableStateOf(ItemDto.EMPTY) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        getItemLoading = true
        state.getSellerItem(
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
                    onReturn = { state.navigateSellerItemsPage() }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                ItemInfo(state = state, item = item)

                ActionButton(
                    text = "Редактировать",
                    action = { state.navigateSellerItemEditPage(itemId) }
                )
            }
        }
    }
}