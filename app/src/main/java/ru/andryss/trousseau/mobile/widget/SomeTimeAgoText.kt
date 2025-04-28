package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import java.time.Duration
import java.time.OffsetDateTime

private const val SECONDS_IN_MINUTE = 60
private const val SECONDS_IN_HOUR = 60 * SECONDS_IN_MINUTE
private const val SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR
private const val DAYS_IN_WEEK = 7
private const val DAYS_IN_MONTH = 30
private const val DAYS_IN_YEAR = 365

@Composable
fun SomeTimeAgoText(timestamp: OffsetDateTime) {

    val seconds = Duration.between(timestamp, OffsetDateTime.now()).seconds

    val text = when {
        seconds < SECONDS_IN_MINUTE -> "< 1 минуты"
        seconds < SECONDS_IN_HOUR -> {
            val minutes = seconds / SECONDS_IN_MINUTE
            "$minutes ${getPluralForm(minutes, listOf("минута", "минуты", "минут"))}"
        }
        seconds < SECONDS_IN_DAY -> {
            val hours = seconds / SECONDS_IN_HOUR
            "$hours ${getPluralForm(hours, listOf("час", "часа", "часов"))}"
        }
        seconds < DAYS_IN_WEEK * SECONDS_IN_DAY -> {
            val days = seconds / SECONDS_IN_DAY
            "$days ${getPluralForm(days, listOf("день", "дня", "дней"))}"
        }
        seconds < DAYS_IN_MONTH * SECONDS_IN_DAY -> {
            val weeks = seconds / (DAYS_IN_WEEK * SECONDS_IN_DAY)
            "$weeks ${getPluralForm(weeks, listOf("неделя", "недели", "недель"))}"
        }
        seconds < DAYS_IN_YEAR * SECONDS_IN_DAY -> {
            val months = seconds / (DAYS_IN_MONTH * SECONDS_IN_DAY)
            "$months ${getPluralForm(months, listOf("месяц", "месяца", "месяцев"))}"
        }
        else -> {
            val years = seconds / (DAYS_IN_YEAR * SECONDS_IN_DAY)
            "$years ${getPluralForm(years, listOf("год", "года", "лет"))}"
        }
    } + " назад"

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        maxLines = 1
    )
}

private fun getPluralForm(value: Long, forms: List<String>): String {
    val mod10 = value % 10
    val mod100 = value % 100
    return when {
        mod10 == 1L && mod100 != 11L -> forms[0]  // один
        mod10 in 2..4 && (mod100 !in 12..14) -> forms[1] // два, три, четыре
        else -> forms[2] // остальные
    }
}

@Preview
@Composable
fun TimeTextPreview() {
    Column {
        SomeTimeAgoText(OffsetDateTime.now().minusSeconds(5))
        SomeTimeAgoText(OffsetDateTime.now().minusMinutes(1))
        SomeTimeAgoText(OffsetDateTime.now().minusMinutes(4))
        SomeTimeAgoText(OffsetDateTime.now().minusMinutes(15))
        SomeTimeAgoText(OffsetDateTime.now().minusHours(11))
        SomeTimeAgoText(OffsetDateTime.now().minusHours(1))
        SomeTimeAgoText(OffsetDateTime.now().minusHours(3))
        SomeTimeAgoText(OffsetDateTime.now().minusDays(1))
        SomeTimeAgoText(OffsetDateTime.now().minusDays(3))
        SomeTimeAgoText(OffsetDateTime.now().minusDays(5))
        SomeTimeAgoText(OffsetDateTime.now().minusWeeks(1))
        SomeTimeAgoText(OffsetDateTime.now().minusWeeks(3))
        SomeTimeAgoText(OffsetDateTime.now().minusMonths(1))
        SomeTimeAgoText(OffsetDateTime.now().minusMonths(3))
        SomeTimeAgoText(OffsetDateTime.now().minusMonths(11))
        SomeTimeAgoText(OffsetDateTime.now().minusYears(1))
        SomeTimeAgoText(OffsetDateTime.now().minusYears(4))
        SomeTimeAgoText(OffsetDateTime.now().minusYears(7))
    }
}