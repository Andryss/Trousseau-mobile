package ru.andryss.trousseau.mobile.client

import android.net.Uri
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import java.io.File
import java.io.FileOutputStream

data class UploadMediaResponse(
    val id: String
)

fun AppState.uploadMedia(
    mediaUri: Uri,
    onSuccess: (id: String) -> Unit,
    onError: (error: String) -> Unit,
) {
    Log.i(TAG, "Send upload media request")
    val file = contentResolver.openInputStream(mediaUri)?.use { input ->
        val tempFile = File.createTempFile("media", "tmp")
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
        tempFile
    } ?: TODO("Error handling")
    val contentType = contentResolver.getType(mediaUri)
    val requestBody = file.asRequestBody(contentType?.toMediaType())
    val body = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("data", mediaUri.path, requestBody)
        .build()
    httpRequest(
        "POST",
        "/seller/media",
        body,
        callbackObj<UploadMediaResponse>(
            onSuccess = {
                Log.i(TAG, "Uploaded new media ${it.id}")
                onSuccess(it.id)
            },
            onError = onError
        )
    )
}
