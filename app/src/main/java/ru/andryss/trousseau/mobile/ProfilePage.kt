package ru.andryss.trousseau.mobile

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.model.createItem

@Composable
fun ProfilePage(state: AppState) {

    var createItemLoading by remember { mutableStateOf(false) }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun onCreateNewItem() {
        createItemLoading = true
        state.createItem(
            onSuccess = {
                Log.i(TAG, it)
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
        if (showAlert) {
            AlertDialog(
                onDismissRequest = { showAlert = false },
                confirmButton = {
                    TextButton(onClick = { showAlert = false }) {
                        Text("OK")
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