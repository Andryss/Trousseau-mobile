package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.PublicItemDto
import ru.andryss.trousseau.mobile.client.getBookings
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.PublicItemCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsSubpage(state: AppState) {

    val bookingsList = remember { mutableStateListOf<PublicItemDto>() }

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
                alertText = it
                showAlert = true
                getBookingsLoading = false
            }
        )
    }

    LaunchedEffect(true) {
        getBookings()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        PullToRefreshBox(
            isRefreshing = getBookingsLoading,
            onRefresh = ::getBookings,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = 10.dp, bottom = 50.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (bookingsList.isEmpty()) {
                    Text("*нет бронирований*")
                } else {
                    for (item in bookingsList) {
                        PublicItemCard(state, item)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (showAlert) {
            AlertDialog(
                onDismissRequest = { showAlert = false },
                confirmButton = {
                    TextButton(onClick = { showAlert = false }) {
                        Text("ОК")
                    }
                },
                icon = { Icon(Icons.Filled.Error, "Error icon") },
                text = { Text(alertText) }
            )
        }
    }
}