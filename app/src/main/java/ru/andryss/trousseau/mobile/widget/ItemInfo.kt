package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.andryss.trousseau.mobile.client.ItemDto

@Composable
fun ItemInfo(item: ItemDto) {

    val imageUris = remember(item) { item.media.map { it.href.toUri() } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = item.title ?: "*пустой заголовок*",
            modifier = Modifier.padding(horizontal = 15.dp),
            style = MaterialTheme.typography.titleLarge
        )
        ImagePager(images = imageUris)
        Text(
            text = item.description ?: "*пустое описание*",
            modifier = Modifier.padding(horizontal = 10.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(70.dp))
    }
}