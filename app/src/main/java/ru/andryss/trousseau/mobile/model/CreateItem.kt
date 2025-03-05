package ru.andryss.trousseau.mobile.model

import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.httpRequest
import java.io.IOException

const val IO_EXCEPTION_ERROR_MESSAGE = "Произошла непредвиденная ошибка, повторите попытку позже"
const val RESPONSE_PARSE_ERROR_MESSAGE = "Произошла ошибка чтения, свяжитесь с поддержкой"
const val ERROR_RESPONSE_MESSAGE_TEMPLATE = "%s (код ошибки: %s)"

data class CreateItemResponse(
    val id: String
)

fun AppState.createItem(
    onSuccess: (id: String) -> Unit,
    onError: (error: String) -> Unit
) {
    Log.i(TAG, "Send create item request")
    httpRequest(
        "POST",
        "/seller/items",
        "{}",
        object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "Error when creating item", e)
                onError(IO_EXCEPTION_ERROR_MESSAGE)
            }
            override fun onResponse(call: Call, response: Response) {
                val mapper = jacksonObjectMapper().apply {
                    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                }
                if (response.code == 200) {
                    response.body?.bytes()?.let {
                        val content = mapper.readValue<CreateItemResponse>(it)
                        Log.i(TAG, "Created new item ${content.id}")
                        onSuccess(content.id)
                    } ?: onError(RESPONSE_PARSE_ERROR_MESSAGE)
                } else {
                    response.body?.bytes()?.let {
                        val error = mapper.readValue<ErrorObject>(it)
                        onError(ERROR_RESPONSE_MESSAGE_TEMPLATE.format(error.humanMessage, error.code))
                    } ?: onError(RESPONSE_PARSE_ERROR_MESSAGE)
                }
            }
        }
    )
}
