package ru.andryss.trousseau.mobile.client

import android.util.Log
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.PropertyNames.Companion.TROUSSEAU_BASE_URL

val JSON_MEDIA_TYPE = "application/json".toMediaType()

fun AppState.authHeaders() =
    mapOf("Authorization" to "Bearer ${userInfo.accessToken}")

@Suppress("CAST_NEVER_SUCCEEDS")
fun AppState.httpRequest(
    method: String,
    url: String,
    callback: Callback,
    headers: Map<String, String> = emptyMap()
) = httpRequest(
    method = method,
    url = url,
    body = null as? String,
    callback = callback,
    headers = headers
)

fun AppState.httpRequest(
    method: String,
    url: String,
    body: String?,
    callback: Callback,
    headers: Map<String, String> = emptyMap()
) = httpRequest(
    method = method,
    url = url,
    body = body?.toRequestBody(JSON_MEDIA_TYPE),
    callback = callback,
    headers = headers
)

fun AppState.httpRequest(
    method: String,
    url: String,
    body: RequestBody?,
    callback: Callback,
    headers: Map<String, String> = emptyMap()
) {
    val call = buildHttpCall(url, method, body, headers)
    call.enqueue(callback)
}

fun AppState.httpRequest(
    method: String,
    url: String,
    body: RequestBody? = null,
    headers: Map<String, String> = emptyMap()
): Response {
    val call = buildHttpCall(url, method, body, headers)
    return call.execute()
}

private fun AppState.buildHttpCall(
    url: String,
    method: String,
    body: RequestBody?,
    headers: Map<String, String>
): Call {
    val baseUrl = properties.getProperty(TROUSSEAU_BASE_URL, "http://localhost:8080")
    val fullUrl = baseUrl + url

    Log.i(TAG, "Sending request $method $fullUrl")
    val request = Request.Builder()
        .url(fullUrl)
        .method(method, body)
        .apply {
            headers.forEach { (name, value) ->
                header(name, value)
            }
        }
        .build()

    return httpClient.newCall(request)
}