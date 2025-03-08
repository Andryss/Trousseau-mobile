package ru.andryss.trousseau.mobile.widgets

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickMultipleVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.util.replaceAllFrom

const val MAX_IMAGES = 10

@Composable
fun MultipleImagePicker(imageUris: SnapshotStateList<Uri>) {

    var selectedImage by remember { mutableIntStateOf(0) }

    val selectLauncher = rememberLauncherForActivityResult(
        PickMultipleVisualMedia(maxItems = MAX_IMAGES)
    ) {
        imageUris.replaceAllFrom(it)
        selectedImage = 0
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ImagePager(
            images = imageUris
        ) {
            IconButton(
                onClick = { selectLauncher.launch(PickVisualMediaRequest(ImageOnly)) },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(10.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    Icons.Default.AddAPhoto,
                    "Add photo button"
                )
            }
            IconButton(
                onClick = { imageUris.clear() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    Icons.Default.DeleteForever,
                    "Add photo button"
                )
            }
        }
    }
}
