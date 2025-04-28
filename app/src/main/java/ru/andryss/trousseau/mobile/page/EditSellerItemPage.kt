package ru.andryss.trousseau.mobile.page

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.media.uploadMedia
import ru.andryss.trousseau.mobile.client.pub.CategoryNode
import ru.andryss.trousseau.mobile.client.seller.UpdateItemInfo
import ru.andryss.trousseau.mobile.client.seller.getSellerItem
import ru.andryss.trousseau.mobile.client.seller.updateSellerItem
import ru.andryss.trousseau.mobile.util.ItemStatus
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.BottomActionButton
import ru.andryss.trousseau.mobile.widget.AlertDialogWrapper
import ru.andryss.trousseau.mobile.widget.CategorySelectorModal
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
    var category by remember { mutableStateOf(CategoryNode.EMPTY) }
    var status by remember { mutableStateOf(ItemStatus.UNKNOWN) }

    var showCategoryModal by remember { mutableStateOf(false) }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun getTitle() = title.trim()
        .replaceRepeatableWhitespace()
        .ifBlank { null }

    fun getDescription() = description.trim()
        .replaceRepeatableWhitespace()
        .ifBlank { null }

    fun getCategory() = category.id
        .ifBlank { null }

    fun isMediaDiffers() = lastSavedMediaUris.size != imageUris.size
            || !lastSavedMediaUris.containsAll(imageUris)

    fun hasLocalChangesMade() = lastSavedState.title != getTitle()
            || lastSavedState.description != getDescription()
            || lastSavedState.category?.id != getCategory()
            || isMediaDiffers()

    fun getItemInfo() =
        UpdateItemInfo(
            getTitle(),
            imageIds,
            getDescription(),
            getCategory(),
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
                alertText = formatError(error)
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
                    alertText = formatError(error)
                    showAlert = true
                }
            )
        }
    }

    fun onSave() =
        updateAsync(
            loadingVar = saveItemLoading,
            onSuccess = { state.navigateSellerItemsPage() }
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
                category = CategoryNode.fromDto(item.category)
                status = item.status
                lastSavedState = item
                lastSavedMediaUris.replaceAllFrom(imageUris)
                getItemLoading = false
            },
            onError = { error ->
                alertText = formatError(error)
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

    AlertDialogWrapper(
        isShown = showAlert,
        onDismiss = { showAlert = false },
        text = alertText
    ) {
        Scaffold(
            topBar = {
                ReturnBackTopBar(
                    title = "Редактирование объявления",
                    onReturn = { state.navigateSellerItemsPage() }
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
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        label = { Text(text = "Название") },
                    )
                    MultipleImagePicker(state, imageUris)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        label = { Text(text = "Описание") },
                        minLines = 5
                    )
                    OutlinedTextField(
                        value = category.name,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        label = { Text(text = "Категория") },
                        readOnly = true,
                        interactionSource = remember { MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is PressInteraction.Release) {
                                            showCategoryModal = true
                                        }
                                    }
                                }
                            }
                    )
                    Spacer(modifier = Modifier.height(70.dp))
                }

                BottomActionButton(
                    text = "Сохранить",
                    action = { onSave() }
                )

                if (showCategoryModal) {
                    CategorySelectorModal(
                        state = state,
                        onSelect = { category = it[0] },
                        isSingleSelect = true,
                        onDismiss = { showCategoryModal = false }
                    )
                }
            }
        }
    }
}

private fun String.replaceRepeatableWhitespace() =
    replace("(\\s)\\1+".toRegex()) { result -> result.groupValues[1] }
