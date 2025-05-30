package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UserProfile(username: String, room: String? = null) {
    val initials = remember(username) {
        val split = username.split("\\s".toRegex())

        val name = if (split.isNotEmpty()) split[0] else ""
        val surname = if (split.size > 1) split[1] else ""

        return@remember (if (name.isBlank()) {
            "??"
        } else if (surname.isBlank()) {
            ("$name?").substring(0, 2)
        } else {
            name.substring(0, 1) + surname.substring(0, 1)
        }).uppercase()
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(50.dp)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Text(
                text = initials,
                style = MaterialTheme.typography.headlineSmall
            )
        }
        Column {
            Text(
                text = username,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1
            )
            room?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}