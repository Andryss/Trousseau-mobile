package ru.andryss.trousseau.mobile.widgets

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade

@Composable
fun ImageWithBlurredFit(uri: Uri) {
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