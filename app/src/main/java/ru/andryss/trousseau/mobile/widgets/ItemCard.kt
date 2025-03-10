package ru.andryss.trousseau.mobile.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.model.ItemDto
import ru.andryss.trousseau.mobile.model.ItemMediaDto
import ru.andryss.trousseau.mobile.navigateItemEditPage
import ru.andryss.trousseau.mobile.util.ItemStatus

@Composable
fun ItemCard(state: AppState, item: ItemDto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .clickable { state.navigateItemEditPage(item.id) },
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            ImagePager(
                images = item.media.map { it.href.toUri() }
            ) {
                val status = try {
                    ItemStatus.valueOf(item.status)
                } catch (e: IllegalArgumentException) {
                    ItemStatus.UNKNOWN
                }
                AssistChip(
                    onClick = {  },
                    label = {
                        Text(
                            text = status.value,
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(horizontal = 10.dp),
                    trailingIcon = {
                        Icon(
                            Icons.Default.MoreHoriz,
                            "Card options icon"
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = status.color.copy(alpha = 0.8f),
                        trailingIconContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Text(
                    text = item.title ?: "*пустой заголовок*",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = item.description ?: "*пустое описание*",
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun ItemCardPreview() {
    val item = ItemDto(
        id = "1234-4321-1234",
        title = "TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE",
        media = listOf(
            ItemMediaDto(id = "123-321", href = "https://sun9-56.userapi.com/s/v1/if1/ckE_uGxsdilADkg6PhXSDEz085PkwaybhjnY7-ImthF1P8c1f0Xf05RM0gMzCYykSM1kOeZ4.jpg?quality=96&as=32x18,48x27,72x40,108x61,160x90,240x135,360x202,480x270,540x304,640x360,720x405,1080x607,1280x720,1440x810,1600x900&from=bu&u=IKBz0svfrhEd8FUISYfVekg2XFYJjaBY5zPcDcszsF4&cs=604x340"),
            ItemMediaDto(id = "234-432", href = "https://images.wallpaperscraft.ru/image/single/gory_ozero_vershiny_129263_800x1280.jpg"),
            ItemMediaDto(id = "345-543", href = "https://static10.tgstat.ru/channels/_0/2d/2df0da7fd2de748e028bdd78ea3845b9.jpg"),
        ),
        description = "description description description description description description description description description description description description description",
        status = "DRAFT"
    )
    ItemCard(AppState(), item)
}