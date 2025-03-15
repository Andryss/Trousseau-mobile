package ru.andryss.trousseau.mobile.util

import androidx.compose.ui.graphics.Color

enum class ItemStatus(
    val value: String,
    val color: Color,
) {
    DRAFT("ЧЕРНОВИК", Color.Gray),
    READY("ГОТОВО К ПУБЛИКАЦИИ", Color.Yellow),
    PUBLISHED("ОПУБЛИКОВАНО", Color.Green),
    BOOKED("ЗАБРОНИРОВАНО", Color.Cyan),
    ARCHIVED("В АРХИВЕ", Color.Magenta),
    UNKNOWN("???", Color.Red)
}
