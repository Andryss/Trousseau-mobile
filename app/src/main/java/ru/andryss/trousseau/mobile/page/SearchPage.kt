package ru.andryss.trousseau.mobile.page

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.FilterInfo
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.PageInfo
import ru.andryss.trousseau.mobile.client.SearchInfo
import ru.andryss.trousseau.mobile.client.SortField
import ru.andryss.trousseau.mobile.client.SortInfo
import ru.andryss.trousseau.mobile.client.SortOrder
import ru.andryss.trousseau.mobile.client.searchItems
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.BottomBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.ItemCard
import ru.andryss.trousseau.mobile.widget.MainTopBar

enum class SortingFilter(
    val label: String,
    val sortInfo: SortInfo
) {
    NEW_FIRST(
        "сначала новые",
        SortInfo(field = SortField.CREATED_AT, order = SortOrder.DESC)
    ),
    OLD_FIRST(
        "сначала старые",
        SortInfo(field = SortField.CREATED_AT, order = SortOrder.ASC)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchPage(state: AppState) {

    var searchItemsLoading by remember { mutableStateOf(false) }

    var searchText by remember { mutableStateOf("") }
    val itemList = remember { mutableStateListOf<ItemDto>() }

    var isFiltersExpanded by remember { mutableStateOf(false) }
    var selectedSorting by remember { mutableStateOf(SortingFilter.NEW_FIRST) }
    var isSortingExpanded by remember { mutableStateOf(false) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun doSearch() {
        searchItemsLoading = true
        state.searchItems(
            SearchInfo(
                text = searchText.trim(),
                sort = selectedSorting.sortInfo,
                filter = FilterInfo(
                    conditions = listOf()
                ),
                page = PageInfo(
                    size = 100,
                    token = null
                ),
            ),
            onSuccess = { items ->
                itemList.replaceAllFrom(items)
                searchItemsLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                searchItemsLoading = false
            }
        )
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Scaffold(
            topBar = { MainTopBar() },
            bottomBar = { BottomBar(state, BottomPage.SEARCH) }
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
                        value = searchText,
                        onValueChange = { searchText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(
                                onClick = { doSearch() }
                            ) {
                                Icon(Icons.Default.Search, "Search items")
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = { doSearch() }
                        )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isFiltersExpanded = !isFiltersExpanded },
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Расширенный поиск")
                            Icon(
                                imageVector = Icons.Default.run {
                                    if (isFiltersExpanded) ArrowDropUp else ArrowDropDown
                                },
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                        if (isFiltersExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Сортировать")
                                    ExposedDropdownMenuBox(
                                        expanded = isSortingExpanded,
                                        onExpandedChange = { isSortingExpanded = it }
                                    ) {
                                        TextField(
                                            value = selectedSorting.label,
                                            onValueChange = { },
                                            modifier = Modifier
                                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                                .width(200.dp),
                                            readOnly = true,
                                            trailingIcon = {
                                                ExposedDropdownMenuDefaults.TrailingIcon(
                                                    expanded = isSortingExpanded
                                                )
                                            }
                                        )
                                        ExposedDropdownMenu(
                                            expanded = isSortingExpanded,
                                            onDismissRequest = { isSortingExpanded = false }
                                        ) {
                                            SortingFilter.entries.forEach { sort ->
                                                DropdownMenuItem(
                                                    text = { Text(sort.label) },
                                                    onClick = {
                                                        selectedSorting = sort
                                                        isSortingExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (searchItemsLoading) {
                            CircularProgressIndicator()
                        } else {
                            if (itemList.isEmpty()) {
                                Text("*нет объявлений*")
                            } else {
                                for (item in itemList) {
                                    ItemCard(state, item, ItemPageCallback.SEARCH)
                                }
                            }
                            Spacer(modifier = Modifier.height(70.dp))
                        }
                    }
                }
            }
        }
    }
}