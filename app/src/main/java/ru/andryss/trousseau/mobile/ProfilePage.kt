package ru.andryss.trousseau.mobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import ru.andryss.trousseau.mobile.model.ItemDto
import ru.andryss.trousseau.mobile.model.createItem
import ru.andryss.trousseau.mobile.model.getItems
import ru.andryss.trousseau.mobile.widgets.ItemCard

@Composable
fun ProfilePage(state: AppState, onItemEdit: (String) -> Unit) {

    val itemList = remember { mutableStateListOf<ItemDto>() }

    var getItemsLoading by remember { mutableStateOf(false) }
    var createItemLoading by remember { mutableStateOf(false) }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        getItemsLoading = true
        state.getItems(
            onSuccess = {
                itemList.clear()
                itemList.addAll(it)
                getItemsLoading = false
            },
            onError = {
                alertText = it
                showAlert = true
                getItemsLoading = false
            }
        )
    }

    fun onCreateNewItem() {
        createItemLoading = true
        state.createItem(
            onSuccess = {
                onItemEdit(it.id)
                createItemLoading = false
            },
            onError = {
                alertText = it
                showAlert = true
                createItemLoading = false
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
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
                    ItemCard(state, item)
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
        FloatingActionButton(
            onClick = { if (!createItemLoading) onCreateNewItem() },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(50.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(70.dp)
            ) {
                if (createItemLoading) {
                    CircularProgressIndicator()
                } else {
                    Icon(Icons.Filled.Add, "Create new icon button")
                }
            }
        }
    }
}