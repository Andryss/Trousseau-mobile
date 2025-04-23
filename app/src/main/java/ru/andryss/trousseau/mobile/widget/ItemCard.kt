package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.AuthorDto
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.ItemMediaDto
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.pub.changeItemFavourite
import ru.andryss.trousseau.mobile.page.ItemPageCallback
import ru.andryss.trousseau.mobile.page.navigateItemPage
import ru.andryss.trousseau.mobile.util.ItemStatus
import java.time.OffsetDateTime

@Composable
fun ItemCard(state: AppState, item: ItemDto, callback: ItemPageCallback) {

    val imageUris = remember { item.media.map { it.href.toUri() } }
    var isFavourite by remember { mutableStateOf(item.isFavourite) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun onChangeFavourite() {
        state.changeItemFavourite(
            itemId = item.id,
            isFavourite = !isFavourite,
            onSuccess = {
                isFavourite = !isFavourite
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert.value = true
            }
        )
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clickable { state.navigateItemPage(item.id, callback) },
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                ImagePager(
                    images = imageUris,
                    content = {
                        IconButton(
                            onClick = { onChangeFavourite() },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                        ) {
                            if (isFavourite) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Favourite",
                                    modifier = Modifier.size(30.dp),
                                    tint = Color.Red
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Favourite border",
                                modifier = Modifier.size(30.dp),
                                tint = Color.Black
                            )
                        }
                    }
                )
                ItemCardContent(item)
            }
        }
    }
}

@Preview
@Composable
fun ItemCardPreview() {
    val item = ItemDto(
        id = "1234-4321-1234",
        author = AuthorDto(
            username = "author",
            contacts = listOf(),
            room = "room 500"
        ),
        title = "TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE",
        media = listOf(
            ItemMediaDto(id = "123-321", href = "https://sun9-56.userapi.com/s/v1/if1/ckE_uGxsdilADkg6PhXSDEz085PkwaybhjnY7-ImthF1P8c1f0Xf05RM0gMzCYykSM1kOeZ4.jpg?quality=96&as=32x18,48x27,72x40,108x61,160x90,240x135,360x202,480x270,540x304,640x360,720x405,1080x607,1280x720,1440x810,1600x900&from=bu&u=IKBz0svfrhEd8FUISYfVekg2XFYJjaBY5zPcDcszsF4&cs=604x340"),
            ItemMediaDto(id = "234-432", href = "https://images.wallpaperscraft.ru/image/single/gory_ozero_vershiny_129263_800x1280.jpg"),
            ItemMediaDto(id = "345-543", href = "https://static10.tgstat.ru/channels/_0/2d/2df0da7fd2de748e028bdd78ea3845b9.jpg"),
        ),
        description = "description description description description description description description description description description description description description",
        status = ItemStatus.PUBLISHED,
        publishedAt = OffsetDateTime.now().minusHours(2)
    )
    ItemCard(AppState(), item, ItemPageCallback.SEARCH)
}