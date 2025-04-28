package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.pub.notifications.NotificationDto
import ru.andryss.trousseau.mobile.client.pub.notifications.getNotifications
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.AlertDialogWrapper
import ru.andryss.trousseau.mobile.widget.BottomNavigationBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.NotificationCard
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar

@Composable
fun NotificationsPage(state: AppState) {

    var getNotificationsLoading by remember { mutableStateOf(false) }

    val notifications = remember { mutableStateListOf<NotificationDto>() }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    LaunchedEffect(true) {
        getNotificationsLoading = true
        state.getNotifications(
            onSuccess = { result ->
                notifications.replaceAllFrom(result)
                getNotificationsLoading = false
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert = true
                getNotificationsLoading = false
            }
        )
    }

    AlertDialogWrapper(
        isShown = showAlert,
        onDismiss = { showAlert = false },
        text = alertText
    ) {
        Scaffold(
            topBar = {
                ReturnBackTopBar(
                    title = "Уведомления",
                    onReturn = { state.navigateProfilePage() }
                )
            },
            bottomBar = { BottomNavigationBar(state = state, page = BottomPage.PROFILE) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (getNotificationsLoading) {
                        CircularProgressIndicator()
                    } else {
                        if (notifications.isEmpty()) {
                            Text("*нет уведомлений*")
                        } else {
                            notifications.forEach {
                                NotificationCard(
                                    state = state,
                                    notification = it
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
