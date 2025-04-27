package ru.andryss.trousseau.mobile.client.auth

data class SignUpRequest(
    val username: String,
    val password: String,
    val contacts: List<String>,
    val room: String?
)

data class SignInRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    val token: String
)

data class ProfileDto(
    val username: String,
    val contacts: List<String>,
    val room: String?,
    val privileges: List<String>,
) {
    companion object {
        val EMPTY = ProfileDto("", listOf(), null, listOf())
    }
}

@Suppress("unused")
enum class Privilege {
    MEDIA_UPLOAD,
    ITEMS_CREATE,
    ITEMS_CREATED_VIEW,
    ITEMS_PUBLISHED_VIEW,
    ITEMS_PUBLISHED_STATUS_CHANGED,
    ITEMS_BOOKINGS_VIEW,
    CATEGORY_TREE_VIEW,
    ITEMS_FAVOURITES,
    SUBSCRIPTIONS_VIEW,
    SUBSCRIPTIONS_EDIT,
    NOTIFICATIONS_VIEW
}

fun ProfileDto.hasPrivilege(privilege: Privilege) =
    privileges.contains(privilege.name)