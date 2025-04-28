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
import ru.andryss.trousseau.mobile.client.auth.Privilege.ITEMS_CREATE
import ru.andryss.trousseau.mobile.client.auth.hasPrivilege
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.seller.getSellerItem
import ru.andryss.trousseau.mobile.util.ITEM_EDITABLE_STATUSES
import ru.andryss.trousseau.mobile.widget.AlertDialogWrapper
import ru.andryss.trousseau.mobile.widget.BottomActionButton
import ru.andryss.trousseau.mobile.widget.ItemPageContent
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar

@Composable
fun ItemPreviewPage(state: AppState, itemId: String) {

    val profile by remember { state.cache.profileCache.profile }

    var getItemLoading by remember { mutableStateOf(false) }

    var item by remember { mutableStateOf(ItemDto.EMPTY) }

    var showAlert by remember { mutableStateOf(false) }
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
                showAlert = true
                getItemLoading = false
            }
        )
    }

    AlertDialogWrapper(
        isShown = showAlert,
        onDismiss = { showAlert = false },
        text = alertText
    ) {
        Scaffold(
            topBar = {
                ReturnBackTopBar(
                    title = "Предпросмотр объявления",
                    onReturn = { state.navigateSellerItemsPage() }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                ItemPageContent(state = state, item = item)

                if (profile.hasPrivilege(ITEMS_CREATE) && item.status in ITEM_EDITABLE_STATUSES) {
                    BottomActionButton(
                        text = "Редактировать",
                        action = { state.navigateSellerItemEditPage(itemId) }
                    )
                }
            }
        }
    }
}