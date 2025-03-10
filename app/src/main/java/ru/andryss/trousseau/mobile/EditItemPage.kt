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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
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
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widgets.MultipleImagePicker

enum class EditItemState(val description: String, val color: Color) {
    LOCAL_CHANGES_MADE("Изменения внесены", Color.Gray),
    UPLOADING_LOCAL_CHANGES("Обновление...", Color.Yellow),
    REMOTE_SYNC("Изменения сохранены", Color.Green)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemPage(state: AppState, itemId: String) {

    var getItemLoading by remember { mutableStateOf(false) }
    val updateItemLoading = remember { mutableStateOf(false) }
    val saveItemLoading = remember { mutableStateOf(false) }

    var lastSavedState by remember { mutableStateOf(ItemDto("", null, listOf(), null, "")) }
    val lastSavedMediaUris = remember { mutableStateListOf<Uri>() }

    var title by remember { mutableStateOf("") }
    val imageUris = remember { mutableStateListOf<Uri>() }
    val imageIds = remember { mutableStateListOf<String>() }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }

    var showAlert by remember { mutableStateOf(false) }
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
        state.updateItem(
            itemId,
            getItemInfo(),
            onSuccess = { item ->
                onSuccess(item)
                loadingVar.value = false
            },
            onError = { error ->
                alertText = error
                showAlert = true
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
                    showAlert = true
                }
            )
        }
    }

    fun onSave() =
        updateAsync(
            loadingVar = saveItemLoading,
            onSuccess = { state.navigateProfilePage() }
        )

    LaunchedEffect(true) {
        getItemLoading = true
        state.getItem(
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Изменение объявления") },
                navigationIcon = {
                    IconButton(
                        onClick = { state.navigateProfilePage() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
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
                // TODO: BEGIN only for debug purposes
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val editState = if (updateItemLoading.value) {
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
    }
}

private fun String.replaceRepeatableWhitespace() =
    replace("(\\s)\\1+".toRegex()) { result -> result.groupValues[1] }
