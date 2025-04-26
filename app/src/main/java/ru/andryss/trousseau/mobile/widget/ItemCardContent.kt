package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.util.Strings

@Composable
fun ItemCardContent(item: ItemDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(
            text = item.title ?: Strings.EMPTY_ITEM_TITLE,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.description ?: Strings.EMPTY_ITEM_DESCRIPTION,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Profile(user = item.author)

            item.publishedAt?.let {
                TimeText(it)
            } ?: Text(
                text = Strings.EMPTY_PUBLISHED_AT,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}
