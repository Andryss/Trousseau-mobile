package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.pub.CategoryNode
import ru.andryss.trousseau.mobile.client.pub.subscriptions.SubscriptionDto
import ru.andryss.trousseau.mobile.client.pub.subscriptions.getSubscriptions
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.AlertWrapper
import ru.andryss.trousseau.mobile.widget.BottomBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.CategorySelectorModal
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar
import ru.andryss.trousseau.mobile.widget.SubscriptionCard

@Composable
fun SubscriptionsPage(state: AppState) {

    var getSubscriptionsLoading by remember { mutableStateOf(false) }

    val subscriptions = remember { mutableStateListOf<SubscriptionDto>() }

    var isAdding by remember { mutableStateOf(false) }
    var newSubName by remember { mutableStateOf("") }
    val newSubCategories = remember { mutableStateListOf<CategoryNode>() }
    var newSubCategoriesText by remember { mutableStateOf("") }

    var showCategoryModal by remember { mutableStateOf(false) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun onCategoriesSelect(nodes: List<CategoryNode>) {
        newSubCategoriesText = nodes.joinToString(
            separator = ",\n",
            transform = { it.name }
        )
        newSubCategories.replaceAllFrom(nodes)
    }

    fun onStartAdd() {
        newSubName = ""
        newSubCategories.clear()
        newSubCategoriesText = ""
        isAdding = true
    }

    fun onSubmitAdd() {
        isAdding = false
    }

    fun onCancelAdd() {
        isAdding = false
    }

    LaunchedEffect(true) {
        getSubscriptionsLoading = true
        state.getSubscriptions(
            onSuccess = { result ->
                subscriptions.replaceAllFrom(result)
                getSubscriptionsLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                getSubscriptionsLoading = false
            }
        )
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Scaffold(
            topBar = {
                ReturnBackTopBar(
                    title = "Подписки",
                    onReturn = { state.navigateProfilePage() }
                )
            },
            bottomBar = { BottomBar(state = state, page = BottomPage.PROFILE) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (getSubscriptionsLoading) {
                        item { CircularProgressIndicator() }
                    } else {
                        items(subscriptions) { subscription ->
                            SubscriptionCard(
                                state = state,
                                subscription = subscription
                            )
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 3.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                            ) {
                                Column(
                                    modifier = Modifier.padding(10.dp)
                                ) {
                                    if (!isAdding) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable { onStartAdd() },
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("Новая подписка")
                                            Icon(Icons.Default.Add, null)
                                        }
                                    } else {
                                        OutlinedTextField(
                                            value = newSubName,
                                            onValueChange = { newSubName = it },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Название") },
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = newSubCategoriesText,
                                            onValueChange = { },
                                            modifier = Modifier.fillMaxWidth(),
                                            label = { Text("Категории") },
                                            readOnly = true,
                                            interactionSource = remember { MutableInteractionSource() }
                                                .also { interactionSource ->
                                                    LaunchedEffect(interactionSource) {
                                                        interactionSource.interactions.collect {
                                                            if (it is PressInteraction.Release) {
                                                                showCategoryModal = true
                                                            }
                                                        }
                                                    }
                                                }
                                        )
                                        Row {
                                            Button(
                                                onClick = { onSubmitAdd() }
                                            ) {
                                                Text("Добавить")
                                            }
                                            OutlinedButton(
                                                onClick = { onCancelAdd() }
                                            ) {
                                                Text("Отменить")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showCategoryModal) {
            CategorySelectorModal(
                state = state,
                onSelect = { onCategoriesSelect(it) },
                isSingleSelect = false,
                onDismiss = { showCategoryModal = false }
            )
        }
    }

}