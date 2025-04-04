package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.widget.BottomBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.MainTopBar

@Composable
fun ProfilePage(state: AppState) {

    Scaffold(
        topBar = { MainTopBar() },
        bottomBar = { BottomBar(state, BottomPage.PROFILE) },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider()
                ProfileRow(
                    text = "Уведомления",
                    icon = Icons.Default.NotificationsNone,
                    onClick = { }
                )
                HorizontalDivider()
                ProfileRow(
                    text = "Избранное",
                    icon = Icons.Default.BookmarkBorder,
                    onClick = { state.navigateFavouritesPage() }
                )
                HorizontalDivider()
                ProfileRow(
                    text = "Мои бронирования",
                    icon = Icons.Default.Lock,
                    onClick = { state.navigateBookingsPage() }
                )
                HorizontalDivider()
                ProfileRow(
                    text = "Мои объявления",
                    icon = Icons.Default.Description,
                    onClick = { state.navigateSellerItemsPage() }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun ProfileRow(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text) },
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
        }
    )
}

@Preview
@Composable
fun NewProfilePagePreview() {
    ProfilePage(
        state = AppState()
    )
}
