package ru.andryss.trousseau.mobile.widget

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import ru.andryss.trousseau.mobile.AppState

@Composable
fun ContactTextField(state: AppState, contact: String) {

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    fun onCopy() {
        clipboard.setText(AnnotatedString(contact))
        Toast.makeText(context, "Скопировано в буфер обмена", LENGTH_SHORT).show()
    }

    fun onOpen() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contact))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            state.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Не удалось открыть ссылку", LENGTH_SHORT).show()
        }
    }

    OutlinedTextField(
        value = contact,
        onValueChange = { },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onCopy() }
                ) {
                    Icon(Icons.Default.ContentCopy, null)
                }
                IconButton(
                    onClick = { onOpen() }
                ) {
                    Icon(Icons.AutoMirrored.Default.OpenInNew, null)
                }
            }
        },
        readOnly = true,
        singleLine = true,
        maxLines = 1
    )
}