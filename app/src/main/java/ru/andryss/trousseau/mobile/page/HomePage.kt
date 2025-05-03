package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.pub.PageInfo
import ru.andryss.trousseau.mobile.client.pub.SearchInfo
import ru.andryss.trousseau.mobile.client.pub.SortField
import ru.andryss.trousseau.mobile.client.pub.SortInfo
import ru.andryss.trousseau.mobile.client.pub.SortOrder
import ru.andryss.trousseau.mobile.client.pub.searchItems
import ru.andryss.trousseau.mobile.widget.AlertDialogWrapper
import ru.andryss.trousseau.mobile.widget.BottomNavigationBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.ItemCard
import ru.andryss.trousseau.mobile.widget.AppNameTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(state: AppState) {

    val context = LocalContext.current

    val fetchSize = 4
    val cache = remember { state.cache.homePageCache }

    var feedLoading by remember { mutableStateOf(false) }
    var refreshItemsLoading by remember { mutableStateOf(false) }

    val feedList = remember { cache.feedItems }
    val listState = rememberLazyListState(cache.visibleItemIndex, cache.visibleItemOffset)
    var isStopFetching by remember { mutableStateOf(false) }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun fetchNextBatch() {
        if (isStopFetching) {
            return
        }
        feedLoading = true
        state.searchItems(
            SearchInfo(
                sort = SortInfo(
                    field = SortField.PUBLISHED_AT,
                    order = SortOrder.DESC,
                ),
                page = PageInfo(
                    size = fetchSize,
                    token = if (feedList.isEmpty()) null else feedList.last().id
                ),
            ),
            onSuccess = { items ->
                feedList.addAll(items)
                feedLoading = false
                refreshItemsLoading = false
                if (items.size < fetchSize) {
                    isStopFetching = true
                }
            },
            onError = { error ->
                if (error.code == 401) {
                    state.signOut(context)
                    return@searchItems
                }
                alertText = formatError(error)
                showAlert = true
                feedLoading = false
            }
        )
    }

    fun onRefresh() {
        refreshItemsLoading = true
        isStopFetching = false
        feedList.clear()
        fetchNextBatch()
    }

    LaunchedEffect(true) {
        if (feedList.isEmpty()) {
            fetchNextBatch()
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .collect { cache.visibleItemIndex = it }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemScrollOffset }
            .collect { cache.visibleItemOffset = it }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex == feedList.size - 1) {
                    fetchNextBatch()
                }
            }
    }

    AlertDialogWrapper(
        isShown = showAlert,
        onDismiss = { showAlert = false },
        text = alertText
    ) {
        Scaffold(
            topBar = { AppNameTopBar() },
            bottomBar = { BottomNavigationBar(state, BottomPage.HOME) }
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
                            Icon(Icons.Default.Search, null)
                        }
                    )
                    PullToRefreshBox(
                        isRefreshing = refreshItemsLoading,
                        onRefresh = { onRefresh() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState,
                            contentPadding = PaddingValues(vertical = 10.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(feedList) { item ->
                                ItemCard(state, item, ItemPageCallback.HOME)
                            }
                            if (feedLoading && !refreshItemsLoading) {
                                item {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}