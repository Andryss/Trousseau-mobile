package ru.andryss.trousseau.widgets

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

const val MAX_IMAGES = 10

@Composable
fun MultipleImagePicker(imageUris: MutableList<Uri>) {

    var selectedImage by remember { mutableIntStateOf(0) }

    val selectLauncher = rememberLauncherForActivityResult(
        PickMultipleVisualMedia(maxItems = MAX_IMAGES)
    ) {
        imageUris.clear()
        imageUris.addAll(it)
        selectedImage = 0
    }

    val addLauncher = rememberLauncherForActivityResult(
        PickMultipleVisualMedia(maxItems = MAX_IMAGES - imageUris.size)
    ) {
        imageUris.addAll(it)
    }

    fun selectImages() =
        selectLauncher.launch(PickVisualMediaRequest(ImageOnly))

    fun addImages() =
        addLauncher.launch(PickVisualMediaRequest(ImageOnly))

    fun clearImages() =
        imageUris.clear()

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PreviewImage(
            imageUri = if (imageUris.isNotEmpty()) imageUris[selectedImage] else null,
            selectImagesFun = { selectImages() }
        )
        if (imageUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentPadding = PaddingValues(horizontal = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                itemsIndexed(imageUris) { index, uri ->
                    ThumbnailImage(
                        modifier = Modifier
                            .clickable { selectedImage = index }
                            .border(
                                width = if (selectedImage == index) 4.dp else 0.dp,
                                color = if (selectedImage == index) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            )
                    ) {
                        ImageWithBlurredFit(uri)
                    }
                }
                if (imageUris.size < MAX_IMAGES) {
                    item {
                        ThumbnailImage(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.secondary)
                                .clickable { addImages() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSecondary,
                                modifier = Modifier.fillMaxSize(fraction = 0.5f)
                            )
                        }
                    }
                }
                item {
                    ThumbnailImage(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.error)
                            .clickable { clearImages() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onError,
                            modifier = Modifier.fillMaxSize(fraction = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PreviewImage(imageUri: Uri?, selectImagesFun: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .clickable { if (imageUri == null) selectImagesFun() },
        contentAlignment = Alignment.Center
    ) {
        if (imageUri != null) {
            ImageWithBlurredFit(imageUri)
        } else {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.fillMaxSize(fraction = 0.5f)
            )
        }
    }
}

@Composable
fun ThumbnailImage(
    modifier: Modifier,
    content: @Composable (BoxScope.() -> Unit)
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(10.dp))
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun ImageWithBlurredFit(uri: Uri) {
    val context = LocalContext.current

    Box {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri)
                .crossfade(enable = true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .blur(20.dp)
        )
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(uri)
                .crossfade(enable = true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
}