package ru.andryss.trousseau.mobile

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
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import ru.andryss.trousseau.mobile.client.PublicItemDto
import ru.andryss.trousseau.mobile.client.getItem
import ru.andryss.trousseau.mobile.widgets.ImagePager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemPage(state: AppState, itemId: String) {

    var getItemLoading by remember { mutableStateOf(false) }

    var item by remember { mutableStateOf(PublicItemDto("", "", listOf(), "")) }
    val imageUris = remember { mutableStateListOf<Uri>() }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun onBlock() {
        /* TODO */
    }

    LaunchedEffect(true) {
        getItemLoading = true
        state.getItem(
            itemId,
            onSuccess = { response ->
                item = response
                imageUris.addAll(response.media.map { it.href.toUri() })
            },
            onError = { error ->
                alertText = error
                showAlert = true
            }
        )
    }

    Box {
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
                        text = item.title,
                        modifier = Modifier.padding(horizontal = 15.dp),
                        style = MaterialTheme.typography.titleLarge
                    )
                    ImagePager(images = imageUris)
                    Text(
                        text = item.description,
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