package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.AuthorDto
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.ItemMediaDto
import ru.andryss.trousseau.mobile.client.UpdateItemStatus
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.seller.BookingDto
import ru.andryss.trousseau.mobile.client.seller.getItemBookingInfo
import ru.andryss.trousseau.mobile.client.seller.updateSellerItemStatus
import ru.andryss.trousseau.mobile.page.navigateSellerItemEditPage
import ru.andryss.trousseau.mobile.page.navigateSellerItemPreviewPage
import ru.andryss.trousseau.mobile.util.ItemStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerItemCard(state: AppState, item: ItemDto) {

    val publishItemLoading = remember { mutableStateOf(false) }
    val unpublishItemLoading = remember { mutableStateOf(false) }
    val unbookItemLoading = remember { mutableStateOf(false) }
    val archiveItemLoading = remember { mutableStateOf(false) }
    var getBookingInfoLoading by remember { mutableStateOf(false) }

    var menuExpanded by remember { mutableStateOf(false) }

    var booking by remember { mutableStateOf(BookingDto.EMPTY) }
    var isBookingShown by remember { mutableStateOf(false) }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    var status by remember { mutableStateOf(item.status) }

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
                alertText = formatError(error)
                showAlert = true
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
        doItemStatusChange(unbookItemLoading, ItemStatus.PUBLISHED)

    fun doItemArchive() =
        doItemStatusChange(archiveItemLoading, ItemStatus.ARCHIVED)

    fun doGetBookingInfo() {
        getBookingInfoLoading = true
        state.getItemBookingInfo(
            item.id,
            onSuccess = { result ->
                booking = result
                isBookingShown = true
                getBookingInfoLoading = false
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert = true
                getBookingInfoLoading = false
                menuExpanded = false
            }
        )
    }

    AlertDialogWrapper(
        isShown = showAlert,
        onDismiss = { showAlert = false },
        text = alertText
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .clickable { state.navigateSellerItemPreviewPage(item.id) },
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
                                        text = "О бронировании",
                                        onClick = { doGetBookingInfo() },
                                        icon = Icons.Default.Person
                                    )
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
                                        onClick = { state.navigateSellerItemEditPage(item.id) },
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
                ItemCardContent(item)
            }
        }

        if (isBookingShown) {
            ModalBottomSheet(
                onDismissRequest = { isBookingShown = false },
                sheetState = rememberModalBottomSheetState(
                    skipPartiallyExpanded = true
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = booking.author.username)
                        SomeTimeAgoText(timestamp = booking.bookedAt)
                    }
                    booking.author.contacts.forEach { contact ->
                        ContactTextField(state = state, contact = contact)
                    }
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
        status = ItemStatus.DRAFT
    )
    SellerItemCard(AppState(), item)
}