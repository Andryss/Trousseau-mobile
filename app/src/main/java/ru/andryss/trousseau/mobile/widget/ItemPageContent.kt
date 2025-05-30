package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.AuthorDto
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.ItemMediaDto
import ru.andryss.trousseau.mobile.util.ItemStatus
import ru.andryss.trousseau.mobile.util.Strings

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ItemPageContent(state: AppState, item: ItemDto) {

    val imageUris = remember(item) { item.media.map { it.href.toUri() } }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = item.title ?: Strings.EMPTY_ITEM_TITLE,
            modifier = Modifier.padding(horizontal = 15.dp),
            style = MaterialTheme.typography.titleLarge
        )
        ImagePager(images = imageUris)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            Text(
                text = item.formatCost(),
                modifier = Modifier.padding(horizontal = 15.dp),
                style = MaterialTheme.typography.titleLarge
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserProfile(username = item.author.username, room = item.author.room)

            item.publishedAt?.let {
                SomeTimeAgoText(it)
            } ?: Text(
                text = Strings.EMPTY_PUBLISHED_AT,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item.author.contacts.forEach { contact ->
                ContactChip(state = state, contact = contact)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = "Описание",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = item.description ?: Strings.EMPTY_ITEM_DESCRIPTION,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = "Категория",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = item.category?.name ?: Strings.EMPTY_ITEM_CATEGORY,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(70.dp))
    }
}

@Composable
@Preview
fun ItemInfoPreview() {
    val item = ItemDto(
        id = "1234-4321",
        author = AuthorDto(
            username = "author",
            contacts = listOf(),
            room = "room 500"
        ),
        title = "Long long long long long long long long long title",
        media = listOf(
            ItemMediaDto(id = "123-321", href = "https://sun9-56.userapi.com/s/v1/if1/ckE_uGxsdilADkg6PhXSDEz085PkwaybhjnY7-ImthF1P8c1f0Xf05RM0gMzCYykSM1kOeZ4.jpg?quality=96&as=32x18,48x27,72x40,108x61,160x90,240x135,360x202,480x270,540x304,640x360,720x405,1080x607,1280x720,1440x810,1600x900&from=bu&u=IKBz0svfrhEd8FUISYfVekg2XFYJjaBY5zPcDcszsF4&cs=604x340"),
            ItemMediaDto(id = "234-432", href = "https://images.wallpaperscraft.ru/image/single/gory_ozero_vershiny_129263_800x1280.jpg"),
            ItemMediaDto(id = "345-543", href = "https://static10.tgstat.ru/channels/_0/2d/2df0da7fd2de748e028bdd78ea3845b9.jpg"),
        ),
        description = """
            Long long long long long long long long long long long long
            Many lines long long long long long long long long long
            long long long long long long long long
            long long long long long long long long long
            Many lines Many lines Many lines
            
            Many lines Many lines Many lines
        """.trimIndent(),
        cost = 5000,
        status = ItemStatus.READY
    )
    ItemPageContent(AppState(), item)
}