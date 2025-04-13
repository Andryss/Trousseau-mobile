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