package ru.andryss.trousseau.mobile.client.pub.subscriptions

import ru.andryss.trousseau.mobile.client.CategoryDto

data class SubscriptionData(
    val categoryIds: List<String>
)

data class SubscriptionDataDto(
    val categories: List<CategoryDto>
)

data class SubscriptionDto(
    val id: String,
    val name: String,
    val data: SubscriptionDataDto
)