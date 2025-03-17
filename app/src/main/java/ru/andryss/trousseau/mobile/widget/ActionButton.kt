package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BoxScope.ActionButton(
    text: String,
    action: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = action,
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(horizontal = 10.dp, vertical = 20.dp),
        enabled = enabled,
    ) {
        Text(text = text)
    }
}