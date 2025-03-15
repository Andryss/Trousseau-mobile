package ru.andryss.trousseau.mobile.page

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.core.net.toUri
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.UpdateItemStatus
import ru.andryss.trousseau.mobile.client.getItem
import ru.andryss.trousseau.mobile.client.updateItemStatus
import ru.andryss.trousseau.mobile.util.ItemStatus
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.ImagePager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPage(state: AppState, itemId: String) {

    var getItemLoading by remember { mutableStateOf(false) }
    var blockItemLoading by remember { mutableStateOf(false) }

    var item by remember { mutableStateOf(ItemDto.EMPTY) }
    val imageUris = remember { mutableStateListOf<Uri>() }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun onBlock() {
        blockItemLoading = true
        state.updateItemStatus(
            item.id,
            UpdateItemStatus(status = ItemStatus.BOOKED),
            onSuccess = {
                state.navigateProfilePage()
                blockItemLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                blockItemLoading = false
            }
        )
    }

    LaunchedEffect(true) {
        getItemLoading = true
        state.getItem(
            itemId,
            onSuccess = { response ->
                item = response
                imageUris.addAll(response.media.map { it.href.toUri() })
                getItemLoading = false
            },
            onError = { error ->
                alertText = error
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
                TopAppBar(
                    title = { Text("Назад") },
                    navigationIcon = {
                        IconButton(
                            onClick = { state.navigateSearchPage() }
                        ) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    },
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = item.title ?: "",
                        modifier = Modifier.padding(horizontal = 15.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    ImagePager(images = imageUris)
                    Text(
                        text = item.description ?: "",
                        modifier = Modifier.padding(horizontal = 10.dp),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(70.dp))
                }

                Button(
                    onClick = { onBlock() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 10.dp, vertical = 20.dp)
                ) {
                    Text(text = "Забронировать")
                }
            }
        }
    }
}