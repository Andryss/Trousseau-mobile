package ru.andryss.trousseau.mobile.widget

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorContact(state: AppState, contact: String) {

    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    var isDrawerShown by remember { mutableStateOf(false) }

    val text = remember { Uri.parse(contact).host ?: contact }

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

    Box(
        modifier = Modifier.height(35.dp)
    ) {
        AssistChip(
            onClick = { isDrawerShown = true },
            label = {
                Text(text)
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.OpenInNew,
                    contentDescription = "Open link",
                    modifier = Modifier.size(AssistChipDefaults.IconSize)
                )
            }
        )
        if (isDrawerShown) {
            ModalBottomSheet(
                onDismissRequest = { isDrawerShown = false },
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(
                                onClick = { onCopy() }
                            ) {
                                Icon(Icons.Default.ContentCopy, null)
                            }
                        },
                        readOnly = true,
                        maxLines = 1
                    )
                    Button(
                        onClick = { onOpen() }
                    ) {
                        Text("Открыть")
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(Icons.AutoMirrored.Default.OpenInNew, null)
                    }
                }
            }
        }
    }
}