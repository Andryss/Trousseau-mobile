package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AlertDialogWrapper(
    isShown: Boolean,
    onDismiss: () -> Unit,
    text: String,
    content: @Composable BoxScope.() -> Unit,
) {
    Box {
        content()

        if (isShown) {
            AlertDialog(
                onDismissRequest = { onDismiss() },
                confirmButton = {
                    TextButton(onClick = { onDismiss() }) {
                        Text("ОК")
                    }
                },
                icon = { Icon(Icons.Filled.Error, "Error icon") },
                text = { Text(text) }
            )
        }
    }
}