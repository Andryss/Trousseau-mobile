package ru.andryss.trousseau.mobile.page

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.UpdateItemInfo
import ru.andryss.trousseau.mobile.client.getSellerItem
import ru.andryss.trousseau.mobile.client.updateSellerItem
import ru.andryss.trousseau.mobile.client.uploadMedia
import ru.andryss.trousseau.mobile.util.ItemStatus
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.ActionButton
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.MultipleImagePicker
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar

@Composable
fun EditSellerItemPage(state: AppState, itemId: String) {

    var getItemLoading by remember { mutableStateOf(false) }
    val updateItemLoading = remember { mutableStateOf(false) }
    val saveItemLoading = remember { mutableStateOf(false) }

    var lastSavedState by remember { mutableStateOf(ItemDto.EMPTY) }
    val lastSavedMediaUris = remember { mutableStateListOf<Uri>() }

    var title by remember { mutableStateOf("") }
    val imageUris = remember { mutableStateListOf<Uri>() }
    val imageIds = remember { mutableStateListOf<String>() }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(ItemStatus.UNKNOWN) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun getTitle() = title.trim()
        .replaceRepeatableWhitespace()
        .ifBlank { null }

    fun getDescription() = description.trim()
        .replaceRepeatableWhitespace()
        .ifBlank { null }

    fun isMediaDiffers() = lastSavedMediaUris.size != imageUris.size
            || !lastSavedMediaUris.containsAll(imageUris)

    fun hasLocalChangesMade() = lastSavedState.title != getTitle()
            || lastSavedState.description != getDescription()
            || isMediaDiffers()

    fun getItemInfo() =
        UpdateItemInfo(
            getTitle(),
            imageIds,
            getDescription()
        )

    fun updateItemInternal(
        onSuccess: (ItemDto) -> Unit,
        loadingVar: MutableState<Boolean>
    ) {
        state.updateSellerItem(
            itemId,
            getItemInfo(),
            onSuccess = { item ->
                onSuccess(item)
                loadingVar.value = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                loadingVar.value = false
            }
        )
    }

    fun updateAsync(loadingVar: MutableState<Boolean>, onSuccess: (ItemDto) -> Unit) {
        loadingVar.value = true
        if (imageUris.isEmpty()) {
            imageIds.clear()
        }
        if (imageUris.isEmpty() || !isMediaDiffers()) {
            updateItemInternal(onSuccess, loadingVar)
            return
        }
        val imageUriIds = mutableMapOf<Uri, String>()
        imageUris.forEach { uri ->
            state.uploadMedia(
                uri,
                onSuccess = { id ->
                    synchronized(imageUriIds) {
                        imageUriIds[uri] = id
                    }
                    if (imageUriIds.size != imageUris.size) return@uploadMedia

                    imageIds.clear()
                    imageUris.forEach { imageIds.add(imageUriIds.getValue(it)) }
                    updateItemInternal(onSuccess, loadingVar)
                },
                onError = { error ->
                    alertText = error
                    showAlert.value = true
                }
            )
        }
    }

    fun onSave() =
        updateAsync(
            loadingVar = saveItemLoading,
            onSuccess = { state.navigateProfileItemsPage() }
        )

    LaunchedEffect(true) {
        getItemLoading = true
        state.getSellerItem(
            id = itemId,
            onSuccess = { item ->
                title = item.title ?: ""
                imageIds.replaceAllFrom(item.media.map { it.id })
                imageUris.replaceAllFrom(item.media.map { it.href.toUri() })
                description = item.description ?: ""
                status = item.status
                lastSavedState = item
                lastSavedMediaUris.replaceAllFrom(imageUris)
                getItemLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                getItemLoading = false
            }
        )
    }

    LaunchedEffect(true) {
        while (true) {
            delay(10_000)
            if (hasLocalChangesMade()) {
                Log.i(TAG, "Changes detected, updating item")
                updateAsync(
                    loadingVar = updateItemLoading,
                    onSuccess = { item ->
                        lastSavedState = item
                        lastSavedMediaUris.replaceAllFrom(imageUris)
                    }
                )
            } else {
                Log.i(TAG, "No item changes, skipping update")
            }
        }
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Scaffold(
            topBar = {
                // TODO: add heading "Изменение объявления"
                ReturnBackTopBar(
                    onReturn = { state.navigateProfileItemsPage() }
                )
            }
        ) { padding ->
            Box(
                modifier = Modifier.padding(padding)
            ) {
                Column(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    TextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        label = { Text(text = "Название") },
                    )
                    MultipleImagePicker(imageUris)
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        label = { Text(text = "Описание") },
                        minLines = 5
                    )
                    Spacer(modifier = Modifier.height(70.dp))
                }

                ActionButton(
                    text = "Сохранить",
                    action = { onSave() }
                )
            }
        }
    }
}

private fun String.replaceRepeatableWhitespace() =
    replace("(\\s)\\1+".toRegex()) { result -> result.groupValues[1] }
