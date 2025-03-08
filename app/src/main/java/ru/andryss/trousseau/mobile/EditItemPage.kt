package ru.andryss.trousseau.mobile

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.delay
import ru.andryss.trousseau.mobile.model.ItemDto
import ru.andryss.trousseau.mobile.model.UpdateItemInfo
import ru.andryss.trousseau.mobile.model.getItem
import ru.andryss.trousseau.mobile.model.updateItem
import ru.andryss.trousseau.mobile.model.uploadMedia
import ru.andryss.trousseau.mobile.widgets.MultipleImagePicker

enum class EditItemState(val description: String, val color: Color) {
    LOCAL_CHANGES_MADE("Изменения внесены", Color.Gray),
    UPLOADING_LOCAL_CHANGES("Обновление...", Color.Yellow),
    REMOTE_SYNC("Изменения сохранены", Color.Green)
}

@Composable
fun EditItemPage(state: AppState, itemId: String) {

    var getItemLoading by remember { mutableStateOf(false) }
    var updateItemLoading by remember { mutableStateOf(false) }
    var saveItemLoading by remember { mutableStateOf(false) }

    var lastSavedState by remember { mutableStateOf(ItemDto("", null, listOf(), null, "")) }
    val lastSavedMediaUris = remember { mutableStateListOf<Uri>() }

    var title by remember { mutableStateOf("") }
    val imageUris = remember { mutableStateListOf<Uri>() }
    val imageIds = remember { mutableStateListOf<String>() }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun getTitle() =
        title.trim().ifBlank { null }

    fun isMediaDiffers() = !lastSavedMediaUris.containsAll(imageUris)
            || !imageUris.containsAll(lastSavedMediaUris)

    fun getDescription() =
        description.trim().ifBlank { null }

    fun hasLocalChangesMade() = lastSavedState.title != getTitle()
            || isMediaDiffers()
            || lastSavedState.description != getDescription()

    fun uploadMedia(i: Int = 0, callback: () -> Unit) {
        state.uploadMedia(
            imageUris[i],
            onSuccess = { id ->
                imageIds.add(id)
                if (i == imageUris.size - 1) {
                    callback()
                } else {
                    uploadMedia(i + 1, callback)
                }
            },
            onError = { error ->
                alertText = error
                showAlert = true
            }
        )
    }

    fun getItemInfo(callback: (UpdateItemInfo) -> Unit) {
        if (isMediaDiffers()) {
            imageIds.clear()
            if (imageUris.isNotEmpty()) {
                uploadMedia {
                    callback(UpdateItemInfo(getTitle(), imageIds, getDescription()))
                }
                return
            }
        }
        callback(UpdateItemInfo(getTitle(), imageIds, getDescription()))
    }

    fun onSave() {
        saveItemLoading = true
        getItemInfo {
            state.updateItem(
                itemId,
                it,
                onSuccess = {
                    state.navigateProfilePage()
                    saveItemLoading = false
                },
                onError = { error ->
                    alertText = error
                    showAlert = true
                    saveItemLoading = false
                }
            )
        }
    }

    LaunchedEffect(true) {
        getItemLoading = true
        state.getItem(
            id = itemId,
            onSuccess = { item ->
                title = item.title ?: ""
                imageIds.apply {
                    clear()
                    addAll(item.media.map { it.id })
                }
                imageUris.apply {
                    clear()
                    addAll(item.media.map { it.href.toUri() })
                }
                description = item.description ?: ""
                status = item.status
                lastSavedState = item
                lastSavedMediaUris.apply {
                    clear()
                    addAll(imageUris)
                }
                getItemLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert = true
                getItemLoading = false
            }
        )
    }

    LaunchedEffect(true) {
        while (true) {
            delay(10_000)
            if (hasLocalChangesMade()) {
                Log.i(TAG, "Changes detected, updating item")
                updateItemLoading = true
                getItemInfo {
                    state.updateItem(
                        itemId,
                        it,
                        onSuccess = { item ->
                            lastSavedState = item
                            lastSavedMediaUris.apply {
                                clear()
                                addAll(imageUris)
                            }
                            updateItemLoading = false
                        },
                        onError = { error ->
                            alertText = error
                            showAlert = true
                            updateItemLoading = false
                        }
                    )
                }
            } else {
                Log.i(TAG, "No item changes, skipping update")
            }
        }
    }

    Column(
        modifier = Modifier
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
            maxLines = 3,
            minLines = 3
        )
        // TODO: BEGIN only for debug purposes
        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val editState = if (updateItemLoading) {
                EditItemState.UPLOADING_LOCAL_CHANGES
            } else if (hasLocalChangesMade()) {
                EditItemState.LOCAL_CHANGES_MADE
            } else {
                EditItemState.REMOTE_SYNC
            }
            Box(
                modifier = Modifier
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(editState.color)
                    .size(16.dp)
            )
            Text(
                text = editState.description
            )
        }
        // TODO: END only for debug purposes
        Button(
            onClick = { onSave() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Text(text = "Сохранить")
        }
    }
}
