package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.ItemMediaDto
import ru.andryss.trousseau.mobile.client.UpdateItemStatus
import ru.andryss.trousseau.mobile.client.updateSellerItemStatus
import ru.andryss.trousseau.mobile.page.navigateSellerItemEditPage
import ru.andryss.trousseau.mobile.util.ItemStatus

@Composable
fun SellerItemCard(state: AppState, item: ItemDto) {

    val publishItemLoading = remember { mutableStateOf(false) }
    val unpublishItemLoading = remember { mutableStateOf(false) }

    var menuExpanded by remember { mutableStateOf(false) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    var status by remember { mutableStateOf(item.status) }

    fun doItemEdit() {
        // TODO: on card click -> show item preview (edit only from chip)
        menuExpanded = false
        state.navigateSellerItemEditPage(item.id)
    }

    fun doItemStatusChange(
        loadingVar: MutableState<Boolean>,
        targetStatus: ItemStatus
    ) {
        loadingVar.value = true
        state.updateSellerItemStatus(
            item.id,
            UpdateItemStatus(status = targetStatus),
            onSuccess = {
                status = targetStatus
                loadingVar.value = false
                menuExpanded = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                loadingVar.value = false
                menuExpanded = false
            }
        )
    }

    fun doItemPublish() =
        doItemStatusChange(publishItemLoading, ItemStatus.PUBLISHED)

    fun doItemUnpublish() =
        doItemStatusChange(unpublishItemLoading, ItemStatus.READY)

    fun doItemUnbook() =
        doItemStatusChange(unpublishItemLoading, ItemStatus.PUBLISHED)

    fun doItemArchive() =
        doItemStatusChange(unpublishItemLoading, ItemStatus.ARCHIVED)

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clickable { doItemEdit() },
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                ImagePager(
                    images = item.media.map { it.href.toUri() }
                ) {
                    AssistChip(
                        onClick = { menuExpanded = true },
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
                            DropdownMenu(
                                expanded = menuExpanded,
                                onDismissRequest = { menuExpanded = false }
                            ) {
                                if (status == ItemStatus.READY) {
                                    SellerItemCardDropDown(
                                        text = "Опубликовать",
                                        onClick = { doItemPublish() },
                                        icon = Icons.Default.Upload
                                    )
                                }
                                if (status == ItemStatus.PUBLISHED) {
                                    SellerItemCardDropDown(
                                        text = "Снять с публикации",
                                        onClick = { doItemUnpublish() },
                                        icon = Icons.Default.Download
                                    )
                                }
                                if (status == ItemStatus.BOOKED) {
                                    SellerItemCardDropDown(
                                        text = "Отказать в бронировании",
                                        onClick = { doItemUnbook() },
                                        icon = Icons.Default.Cancel
                                    )
                                    SellerItemCardDropDown(
                                        text = "Закрыть объявление",
                                        onClick = { doItemArchive() },
                                        icon = Icons.Default.Flag
                                    )
                                }
                                if (status in listOf(ItemStatus.DRAFT, ItemStatus.READY)) {
                                    SellerItemCardDropDown(
                                        text = "Редактировать",
                                        onClick = { doItemEdit() },
                                        icon = Icons.Default.Edit
                                    )
                                }
                            }
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
}

@Composable
fun SellerItemCardDropDown(
    text: String,
    onClick: () -> Unit,
    icon: ImageVector
) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        onClick = { onClick() },
        leadingIcon = { Icon(icon, text) }
    )
}

@Preview
@Composable
fun SellerItemCardPreview() {
    val item = ItemDto(
        id = "1234-4321-1234",
        title = "TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE TITLE",
        media = listOf(
            ItemMediaDto(id = "123-321", href = "https://sun9-56.userapi.com/s/v1/if1/ckE_uGxsdilADkg6PhXSDEz085PkwaybhjnY7-ImthF1P8c1f0Xf05RM0gMzCYykSM1kOeZ4.jpg?quality=96&as=32x18,48x27,72x40,108x61,160x90,240x135,360x202,480x270,540x304,640x360,720x405,1080x607,1280x720,1440x810,1600x900&from=bu&u=IKBz0svfrhEd8FUISYfVekg2XFYJjaBY5zPcDcszsF4&cs=604x340"),
            ItemMediaDto(id = "234-432", href = "https://images.wallpaperscraft.ru/image/single/gory_ozero_vershiny_129263_800x1280.jpg"),
            ItemMediaDto(id = "345-543", href = "https://static10.tgstat.ru/channels/_0/2d/2df0da7fd2de748e028bdd78ea3845b9.jpg"),
        ),
        description = "description description description description description description description description description description description description description",
        status = ItemStatus.DRAFT
    )
    SellerItemCard(AppState(), item)
}