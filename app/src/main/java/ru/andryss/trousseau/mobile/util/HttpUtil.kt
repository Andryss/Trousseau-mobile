package ru.andryss.trousseau.mobile.util

import android.util.Log
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.PropertyNames.Companion.TROUSSEAU_BASE_URL

val JSON_MEDIA_TYPE = "application/json".toMediaType()

fun AppState.httpRequest(method: String, url: String, body: String?, callback: Callback) {
    val baseUrl = properties.getProperty(TROUSSEAU_BASE_URL, "http://localhost:8080")
    val fullUrl = baseUrl + url

    Log.i(TAG, "Sending request $method $fullUrl")
    val request = Request.Builder()
        .url(fullUrl)
        .method(method, body?.toRequestBody(JSON_MEDIA_TYPE))
        .build()

    val call = httpClient.newCall(request)

    call.enqueue(callback)
}