package ru.andryss.trousseau.mobile.widget

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorContact(state: AppState, contact: String) {

    var isDrawerShown by remember { mutableStateOf(false) }

    val text = remember {
        val uri = Uri.parse(contact).host ?: contact
        return@remember if (uri.length < 15) {
            uri
        } else {
            uri.substring(0, 12) + "..."
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
                    ContactTextField(state = state, contact = contact)
                }
            }
        }
    }
}