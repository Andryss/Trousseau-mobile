package ru.andryss.trousseau.mobile.widget

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

val todayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
val yesterdayFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("вчера, HH:mm")
val beforeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM, HH:mm")

@Composable
fun NotificationTimestamp(timestamp: OffsetDateTime) {
    val zone = remember { ZoneId.systemDefault() }
    val timestampWithZone = remember { timestamp.atZoneSameInstant(zone) }
    val startOfDay = remember { LocalDate.now().atStartOfDay().atZone(zone) }

    val text = remember {
        if (timestampWithZone.isAfter(startOfDay)) {
            todayFormatter.format(timestampWithZone)
        } else if (timestampWithZone.isAfter(startOfDay.minusDays(1))) {
            yesterdayFormatter.format(timestampWithZone)
        } else {
            beforeFormatter.format(timestampWithZone)
        }
    }

    Text(text = text)
}