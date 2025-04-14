package ru.andryss.trousseau.mobile.client.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.client.authHeaders
import ru.andryss.trousseau.mobile.client.callbackObj
import ru.andryss.trousseau.mobile.client.httpRequest
import java.io.ByteArrayOutputStream
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
    val file = try {
        compressImage(mediaUri)
    } catch (e: Exception) {
        Log.e(TAG, "Error occurred during file compressing", e)
        onError("Внутренняя ошибка, попробуйте еще раз позже")
        return
    }
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
        ),
        authHeaders(),
    )
}

fun Context.compressImage(imageUri: Uri, maxSize: Int = 300 * 1024): File {
    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(contentResolver, imageUri)
        ImageDecoder.decodeBitmap(source)
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
    }

    var quality = 70
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

    while (outputStream.size() > maxSize && quality > 5) {
        quality = (quality * 0.7).toInt()
        outputStream.reset()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    }

    val compressedFile = File.createTempFile("media", "tmp")
    FileOutputStream(compressedFile).use { it.write(outputStream.toByteArray()) }

    return compressedFile
}
