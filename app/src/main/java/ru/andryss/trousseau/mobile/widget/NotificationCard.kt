package ru.andryss.trousseau.mobile.widget

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.pub.notifications.NotificationDto
import ru.andryss.trousseau.mobile.client.pub.notifications.markNotificationRead
import ru.andryss.trousseau.mobile.page.ItemPageCallback
import ru.andryss.trousseau.mobile.page.navigateItemPage
import ru.andryss.trousseau.mobile.page.navigateSellerItemsPage
import ru.andryss.trousseau.mobile.page.navigateSubscriptionsPage
import java.time.OffsetDateTime


@Composable
fun NotificationCard(state: AppState, notification: NotificationDto) {

    var isRead by remember { mutableStateOf(notification.isRead) }

    fun onCardClick() {
        if (isRead) {
            return
        }
        isRead = true
        state.markNotificationRead(
            notification.id,
            onSuccess = {
                Log.i(TAG, "Notification ${notification.id} marked successfully")
            },
            onError = { error ->
                Log.e(TAG, "Notification ${notification.id} mark failed: $error")
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = notification.title,
                    modifier = Modifier.width(270.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Timestamp(notification.timestamp)
            }
            Text(
                text = notification.content,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold,
            )
            if (notification.links.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    notification.links.forEach { link ->
                        NotificationLink(
                            state = state,
                            link = link
                        )
                    }
                }
            }
        }
    }
}

val notificationEntities: Map<String, String> = mapOf(
    "item" to "Объявление",
    "sellerItem" to "Объявление",
    "subscription" to "Подписка"
)

val notificationActions: Map<String, (AppState, String) -> Unit> = mapOf(
    "item" to { state, id -> state.navigateItemPage(id, ItemPageCallback.HOME) },
    "sellerItem" to { state, _ -> state.navigateSellerItemsPage() },
    "subscription" to { state, _ -> state.navigateSubscriptionsPage() }
)

@Composable
fun NotificationLink(state: AppState, link: String) {
    val terms = remember { link.split(":") }

    val entity = remember { terms[0] }
    val id = remember { terms[1] }

    val text = remember { notificationEntities.getOrDefault(entity, "???") }
    val action = remember { notificationActions.getOrDefault(entity) { _, _ -> } }

    AssistChip(
        onClick = {
            action(state, id)
        },
        label = {
            Text(text = text)
        }
    )
}

@SuppressLint("NewApi")
@Preview
@Composable
fun NotificationCardPreview() {
    Column {
        NotificationCard(
            state = AppState(),
            notification = NotificationDto(
                id = "some-id",
                title = "some long long long long long long long long long title",
                content = "some content content content content content content content content content content content content content content content content content content",
                links = listOf(
                    "item:1234", "subscription:1234"
                ),
                timestamp = OffsetDateTime.now(),
                isRead = false
            )
        )
        NotificationCard(
            state = AppState(),
            notification = NotificationDto(
                id = "some-id",
                title = "some long long long long long long long long long title",
                content = "some content content content content content content content content content content content content content content content content content content",
                links = listOf(
                    "item:1234", "subscription:1234"
                ),
                timestamp = OffsetDateTime.now().minusDays(1),
                isRead = false
            )
        )
        NotificationCard(
            state = AppState(),
            notification = NotificationDto(
                id = "some-id",
                title = "some long long long long long long long long long title",
                content = "some content content content content content content content content content content content content content content content content content content",
                links = listOf(
                    "item:1234", "subscription:1234"
                ),
                timestamp = OffsetDateTime.now().minusDays(2),
                isRead = false
            )
        )
    }
}