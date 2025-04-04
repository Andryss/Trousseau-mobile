package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.seller.createSellerItem
import ru.andryss.trousseau.mobile.client.seller.getSellerItems
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.BottomBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar
import ru.andryss.trousseau.mobile.widget.SellerItemCard

@Composable
fun MyItemsPage(state: AppState) {

    val itemList = remember { mutableStateListOf<ItemDto>() }

    var getItemsLoading by remember { mutableStateOf(false) }
    var createItemLoading by remember { mutableStateOf(false) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun getItems() {
        getItemsLoading = true
        state.getSellerItems(
            onSuccess = {
                itemList.replaceAllFrom(it)
                getItemsLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                getItemsLoading = false
            }
        )
    }

    fun onCreateNewItem() {
        createItemLoading = true
        state.createSellerItem(
            onSuccess = {
                state.navigateSellerItemEditPage(it.id)
                createItemLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                createItemLoading = false
            }
        )
    }

    LaunchedEffect(true) {
        getItems()
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Scaffold(
            topBar = {
                ReturnBackTopBar(
                    title = "Мои объявления",
                    onReturn = { state.navigateProfilePage() }
                )
            },
            bottomBar = { BottomBar(state = state, page = BottomPage.PROFILE) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { if (!createItemLoading) onCreateNewItem() }
                ) {
                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        if (createItemLoading) {
                            CircularProgressIndicator()
                        } else {
                            Icon(Icons.Filled.Add, "Create new icon button")
                        }
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 10.dp, bottom = 50.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (getItemsLoading) {
                        CircularProgressIndicator()
                    } else {
                        if (itemList.isEmpty()) {
                            Text("*нет объявлений*")
                        } else {
                            for (item in itemList) {
                                SellerItemCard(state, item)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}