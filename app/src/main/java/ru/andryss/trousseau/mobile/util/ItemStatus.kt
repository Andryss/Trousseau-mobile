package ru.andryss.trousseau.mobile.util

import androidx.compose.ui.graphics.Color

enum class ItemStatus(
    val value: String,
    val color: Color,
) {
    DRAFT("ЧЕРНОВИК", Color(0xFFFFC107)),
    READY("ГОТОВО К ПУБЛИКАЦИИ", Color(0xFF2196F3)),
    PUBLISHED("ОПУБЛИКОВАНО", Color(0xFF4CAF50)),
    BOOKED("ЗАБРОНИРОВАНО", Color(0xFF9C27B0)),
    ARCHIVED("В АРХИВЕ", Color(0xFF757575)),
    UNKNOWN("???", Color(0xFFF44336))
}

val ITEM_EDITABLE_STATUSES = listOf(ItemStatus.DRAFT, ItemStatus.READY)
