package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
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
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.getFeed
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.BottomBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.ItemCard
import ru.andryss.trousseau.mobile.widget.MainTopBar

@Composable
fun HomePage(state: AppState) {

    var getFeedLoading by remember { mutableStateOf(false) }

    val feedList = remember { mutableStateListOf<ItemDto>() }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun doGetFeed() {
        getFeedLoading = true
        state.getFeed(
            onSuccess = { items ->
                feedList.replaceAllFrom(items)
                getFeedLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                getFeedLoading = false
            }
        )
    }

    LaunchedEffect(true) {
        doGetFeed()
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Scaffold(
            topBar = { MainTopBar() },
            bottomBar = { BottomBar(state, BottomPage.HOME) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextField(
                        value = "Поиск",
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { state.navigateSearchPage() },
                        enabled = false,
                        trailingIcon = {
                            Icon(Icons.Default.Search, "Search items")
                        }
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        for (item in feedList) {
                            ItemCard(state, item, ItemPageCallback.HOME)
                        }
                        if (getFeedLoading) {
                            CircularProgressIndicator()
                        }
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }
    }
}