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
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.PageInfo
import ru.andryss.trousseau.mobile.client.SearchInfo
import ru.andryss.trousseau.mobile.client.SortField
import ru.andryss.trousseau.mobile.client.SortInfo
import ru.andryss.trousseau.mobile.client.SortOrder
import ru.andryss.trousseau.mobile.client.searchItems
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.BottomBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.ItemCard
import ru.andryss.trousseau.mobile.widget.MainTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePage(state: AppState) {

    val fetchSize = 4
    val cache = remember { state.cache.homePageCache }

    var feedLoading by remember { mutableStateOf(false) }
    var refreshItemsLoading by remember { mutableStateOf(false) }

    val feedList = remember { cache.feedItems }
    val listState = rememberLazyListState(cache.visibleItemIndex, cache.visibleItemOffset)
    var isStopFetching by remember { mutableStateOf(false) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun fetchNextBatch() {
        if (isStopFetching) {
            return
        }
        feedLoading = true
        state.searchItems(
            SearchInfo(
                sort = SortInfo(
                    field = SortField.CREATED_AT,
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
                alertText = error
                showAlert.value = true
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