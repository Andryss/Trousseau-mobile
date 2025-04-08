package ru.andryss.trousseau.mobile.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.CategoryDto
import ru.andryss.trousseau.mobile.client.pub.subscriptions.SubscriptionDataDto
import ru.andryss.trousseau.mobile.client.pub.subscriptions.SubscriptionDto
import ru.andryss.trousseau.mobile.client.pub.subscriptions.SubscriptionInfoRequest
import ru.andryss.trousseau.mobile.client.pub.subscriptions.deleteSubscription
import ru.andryss.trousseau.mobile.client.pub.subscriptions.updateSubscription

@Composable
fun SubscriptionCard(state: AppState, dto: SubscriptionDto, onSubscriptionDelete: () -> Unit) {

    var subscription by remember { mutableStateOf(dto) }

    var deleteSubscriptionLoading by remember { mutableStateOf(false) }
    var updateSubscriptionLoading by remember { mutableStateOf(false) }

    var isExpanded by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }

    val categoriesText = remember(subscription) {
        subscription.data.categories.joinToString(
            separator = ",\n",
            transform = { it.name }
        )
    }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun onEditStart() {
        isEditing = true
    }

    fun onEditFinish(request: SubscriptionInfoRequest) {
        updateSubscriptionLoading = true
        state.updateSubscription(
            subscription.id,
            request,
            onSuccess = { result ->
                subscription = result
                isEditing = false
                updateSubscriptionLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                updateSubscriptionLoading = false
            }
        )
    }

    fun onEditCancel() {
        isEditing = false
    }

    fun onDelete() {
        deleteSubscriptionLoading = true
        state.deleteSubscription(
            subscription.id,
            onSuccess = {
                onSubscriptionDelete()
                deleteSubscriptionLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                deleteSubscriptionLoading = false
            }
        )
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            if (isEditing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 5.dp)
                ) {
                    SubscriptionForm(
                        state = state,
                        reference = subscription,
                        onSubmit = { onEditFinish(it)},
                        onCancel = { onEditCancel() }
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clickable { isExpanded = !isExpanded },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subscription.name,
                        modifier = Modifier
                            .padding(10.dp)
                            .width(330.dp),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Icon(
                        imageVector = Icons.Default.run { if (isExpanded) ExpandLess else ExpandMore },
                        contentDescription = "Expand/Compose",
                        modifier = Modifier.padding(10.dp)
                    )
                }
                if (isExpanded) {
                    HorizontalDivider()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Категории",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(categoriesText)
                    }
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onEditStart() }
                        ) {
                            Text("Редактировать")
                        }
                        OutlinedButton(
                            onClick = { onDelete() },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Удалить")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SubscriptionCardPreview() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Spacer(modifier = Modifier.height(50.dp))
        SubscriptionCard(
            state = AppState(),
            dto = SubscriptionDto(
                id = "some-id",
                name = "some long long long long long long long long name",
                data = SubscriptionDataDto(
                    categories = listOf(
                        CategoryDto("id1", "some-name"),
                        CategoryDto("id2", "some-name"),
                        CategoryDto("id3", "some-name"),
                        CategoryDto("id4", "some-name"),
                        CategoryDto("id5", "some-name")
                    )
                )
            ),
            onSubscriptionDelete = { }
        )
    }
}