package ru.andryss.trousseau

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
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

@Composable
fun CreateItemPage() {
    var name by remember { mutableStateOf("") }
    val imageUris = remember { mutableStateListOf<Uri>() }
    var selectedImage by remember { mutableIntStateOf(0) }

    val launcher = rememberLauncherForActivityResult(PickMultipleVisualMedia(maxItems = 10)) {
        imageUris.clear()
        imageUris.addAll(it)
        selectedImage = 0
    }

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Название") },
        )
        Button(
            onClick = { launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
        ) {
            Text(text = "Pick Image")
        }
        if (imageUris.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(5.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                ImageWithBlurredFit(imageUris[selectedImage])
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                itemsIndexed(imageUris) { index, uri ->
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .padding(5.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedImage = index }
                            .border(
                                width = if (selectedImage == index) 4.dp else 0.dp,
                                color = if (selectedImage == index) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        ImageWithBlurredFit(uri)
                    }
                }
            }
        }
    }
}

@Composable
private fun ImageWithBlurredFit(uri: Uri) {
    val context = LocalContext.current

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
