package ru.andryss.trousseau.mobile.model

data class ErrorObject(
    val code: Int,
    val message: String,
    val humanMessage: String
)