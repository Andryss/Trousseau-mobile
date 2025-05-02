package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.pub.getBookings
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.AlertDialogWrapper
import ru.andryss.trousseau.mobile.widget.BottomNavigationBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.ItemCard
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar

@Composable
fun MyBookingsPage(state: AppState) {

    val bookingsList = remember { mutableStateListOf<ItemDto>() }

    var getBookingsLoading by remember { mutableStateOf(false) }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun getBookings() {
        getBookingsLoading = true
        state.getBookings(
            onSuccess = {
                bookingsList.replaceAllFrom(it)
                getBookingsLoading = false
            },
            onError = {
                alertText = formatError(it)
                showAlert = true
                getBookingsLoading = false
            }
        )
    }

    LaunchedEffect(true) {
        getBookings()
    }

    AlertDialogWrapper(
        isShown = showAlert,
        onDismiss = { showAlert = false },
        text = alertText
    ) {
        Scaffold(
            topBar = {
                ReturnBackTopBar(
                    title = "Мои бронирования",
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(top = 10.dp, bottom = 50.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (getBookingsLoading) {
                        CircularProgressIndicator()
                    } else {
                        if (bookingsList.isEmpty()) {
                            Text(
                                text = "Активных бронирований нет",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QuestionMark,
                                    contentDescription = null
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        appendLine("Чтобы забронировать объявление - откройте")
                                        append("интересующее объявление и нажмите «Забронировать»")
                                    },
                                    inlineContent = mapOf(
                                        "heart" to InlineTextContent(
                                            placeholder = Placeholder(
                                                width = 20.sp,
                                                height = 20.sp,
                                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.FavoriteBorder,
                                                contentDescription = "Favorite"
                                            )
                                        }
                                    ),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            for (item in bookingsList) {
                                ItemCard(state, item, ItemPageCallback.BOOKINGS)
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}