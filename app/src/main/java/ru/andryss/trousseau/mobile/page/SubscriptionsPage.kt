package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import ru.andryss.trousseau.mobile.client.auth.Privilege.SUBSCRIPTIONS_EDIT
import ru.andryss.trousseau.mobile.client.auth.hasPrivilege
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.pub.subscriptions.SubscriptionDto
import ru.andryss.trousseau.mobile.client.pub.subscriptions.SubscriptionInfoRequest
import ru.andryss.trousseau.mobile.client.pub.subscriptions.createSubscription
import ru.andryss.trousseau.mobile.client.pub.subscriptions.getSubscriptions
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.AlertDialogWrapper
import ru.andryss.trousseau.mobile.widget.BottomNavigationBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar
import ru.andryss.trousseau.mobile.widget.SubscriptionCard
import ru.andryss.trousseau.mobile.widget.SubscriptionForm

@Composable
fun SubscriptionsPage(state: AppState) {

    val profile by remember { state.cache.profileCache.profile }

    var getSubscriptionsLoading by remember { mutableStateOf(false) }
    var createSubscriptionLoading by remember { mutableStateOf(false) }

    val subscriptions = remember { mutableStateListOf<SubscriptionDto>() }

    var isAdding by remember { mutableStateOf(false) }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun onStartAdd() {
        isAdding = true
    }

    fun onSubmitAdd(request: SubscriptionInfoRequest) {
        createSubscriptionLoading = true
        state.createSubscription(
            request,
            onSuccess = { subscription ->
                subscriptions.add(subscription)
                isAdding = false
                createSubscriptionLoading = false
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert = true
                createSubscriptionLoading = false
            }
        )
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
                alertText = formatError(error)
                showAlert = true
                getSubscriptionsLoading = false
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
                    title = "Подписки",
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
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (getSubscriptionsLoading) {
                        item { CircularProgressIndicator() }
                    } else {
                        items(subscriptions) { subscription ->
                            SubscriptionCard(
                                state = state,
                                dto = subscription,
                                onSubscriptionDelete = { subscriptions.remove(subscription) }
                            )
                        }
                        if (profile.hasPrivilege(SUBSCRIPTIONS_EDIT)) {
                            item {
                                if (isAdding) {
                                    Card {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(5.dp)
                                        ) {
                                            SubscriptionForm(
                                                state = state,
                                                onSubmit = { onSubmitAdd(it) },
                                                onCancel = { onCancelAdd() }
                                            )
                                        }
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { onStartAdd() }
                                    ) {
                                        Text("Новая подписка")
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