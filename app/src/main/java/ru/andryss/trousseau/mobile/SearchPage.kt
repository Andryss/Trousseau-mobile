package ru.andryss.trousseau.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.client.PublicItemDto
import ru.andryss.trousseau.mobile.client.SearchInfo
import ru.andryss.trousseau.mobile.client.searchItems
import ru.andryss.trousseau.mobile.widgets.BottomBar
import ru.andryss.trousseau.mobile.widgets.BottomPage
import ru.andryss.trousseau.mobile.widgets.PublicItemCard
import ru.andryss.trousseau.mobile.widgets.TopBar

@Composable
fun SearchPage(state: AppState) {

    var searchItemsLoading by remember { mutableStateOf(false) }

    var searchText by remember { mutableStateOf("") }
    val itemList = remember { mutableStateListOf<PublicItemDto>() }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun doSearch() {
        searchItemsLoading = true
        state.searchItems(
            SearchInfo(searchText),
            onSuccess = { items ->
                itemList.clear()
                itemList.addAll(items)
                searchItemsLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert = true
                searchItemsLoading = false
            }
        )
    }

    Box {
        Scaffold(
            topBar = { TopBar() },
            bottomBar = { BottomBar(state, BottomPage.SEARCH) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = { doSearch() }
                            ) {
                                Icon(Icons.Default.Search, "Search items")
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { doSearch() }
                        )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(top = 10.dp, bottom = 50.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (itemList.isEmpty()) {
                            Text("*нет объявлений*")
                        } else {
                            for (item in itemList) {
                                PublicItemCard(state, item)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        if (showAlert) {
            AlertDialog(
                onDismissRequest = { showAlert = false },
                confirmButton = {
                    TextButton(onClick = { showAlert = false }) {
                        Text("ОК")
                    }
                },
                icon = { Icon(Icons.Filled.Error, "Error icon") },
                text = { Text(alertText) }
            )
        }
    }
}