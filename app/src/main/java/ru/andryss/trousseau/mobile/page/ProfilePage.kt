package ru.andryss.trousseau.mobile.page

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.auth.Privilege.ITEMS_BOOKINGS_VIEW
import ru.andryss.trousseau.mobile.client.auth.Privilege.ITEMS_CREATED_VIEW
import ru.andryss.trousseau.mobile.client.auth.Privilege.ITEMS_FAVOURITES
import ru.andryss.trousseau.mobile.client.auth.Privilege.NOTIFICATIONS_VIEW
import ru.andryss.trousseau.mobile.client.auth.Privilege.SUBSCRIPTIONS_VIEW
import ru.andryss.trousseau.mobile.client.auth.hasPrivilege
import ru.andryss.trousseau.mobile.client.auth.signOut
import ru.andryss.trousseau.mobile.client.auth.updateProfileInfo
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.pub.notifications.getUnreadNotificationsCount
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.BottomBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.MainTopBar

@Composable
fun ProfilePage(state: AppState) {

    val context = LocalContext.current

    var logoutLoading by remember { mutableStateOf(false) }

    var unreadNotifications by remember { mutableIntStateOf(0) }

    var isShowLogoutDialog by remember { mutableStateOf(false) }

    val profile by remember { state.cache.profileCache.profile }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun logOut() {
        logoutLoading = true
        state.signOut(
            onSuccess = {
                state.signOut(context)
                logoutLoading = false
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert.value = true
                logoutLoading = false
            }
        )
    }

    LaunchedEffect(true) {
        state.getUnreadNotificationsCount(
            onSuccess = { count ->
                unreadNotifications = count.coerceAtMost(99)
            },
            onError = { error ->
                Log.e(TAG, "Got error while fetching unread notifications count: $error")
            }
        )
    }

    LaunchedEffect(true) {
        state.updateProfileInfo()
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
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
                    if (profile.hasPrivilege(NOTIFICATIONS_VIEW)) {
                        HorizontalDivider()
                        ProfileRow(
                            text = "Уведомления",
                            icon = {
                                Box {
                                    Icon(Icons.Default.NotificationsNone, null)
                                    if (unreadNotifications > 0) {
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .offset(x = 4.dp, y = (-4).dp)
                                                .size(15.dp)
                                                .background(
                                                    color = MaterialTheme.colorScheme.error,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.TopCenter
                                        ) {
                                            Text(
                                                text = unreadNotifications.toString(),
                                                color = MaterialTheme.colorScheme.onError,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                lineHeight = 15.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            },
                            onClick = { state.navigateNotificationsPage() }
                        )
                    }
                    if (profile.hasPrivilege(SUBSCRIPTIONS_VIEW)) {
                        HorizontalDivider()
                        ProfileRow(
                            text = "Подписки",
                            icon = {
                                Icon(Icons.Default.Alarm, null)
                            },
                            onClick = { state.navigateSubscriptionsPage() }
                        )
                    }
                    if (profile.hasPrivilege(ITEMS_FAVOURITES)) {
                        HorizontalDivider()
                        ProfileRow(
                            text = "Избранное",
                            icon = {
                                Icon(Icons.Default.BookmarkBorder, null)
                            },
                            onClick = { state.navigateFavouritesPage() }
                        )
                    }
                    if (profile.hasPrivilege(ITEMS_BOOKINGS_VIEW)) {
                        HorizontalDivider()
                        ProfileRow(
                            text = "Мои бронирования",
                            icon = {
                                Icon(Icons.Default.Lock, null)
                            },
                            onClick = { state.navigateBookingsPage() }
                        )
                    }
                    if (profile.hasPrivilege(ITEMS_CREATED_VIEW)) {
                        HorizontalDivider()
                        ProfileRow(
                            text = "Мои объявления",
                            icon = {
                                Icon(Icons.Default.Description, null)
                            },
                            onClick = { state.navigateSellerItemsPage() }
                        )
                    }
                    HorizontalDivider()
                    ProfileRow(
                        text = "Выйти",
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        onClick = { isShowLogoutDialog = true }
                    )
                    HorizontalDivider()
                }
            }
        }

        if (isShowLogoutDialog) {
            AlertDialog(
                onDismissRequest = { isShowLogoutDialog = false },
                confirmButton = {
                    TextButton(
                        onClick = { logOut() }
                    ) {
                        Text("Выйти")
                    }
                },
                icon = { Icon(Icons.Filled.QuestionMark, null) },
                text = {
                    Text("Выйти из учетной записи?")
                }
            )
        }
    }
}

@Composable
fun ProfileRow(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(text) },
        modifier = Modifier.clickable { onClick() },
        leadingContent = {
            icon()
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
