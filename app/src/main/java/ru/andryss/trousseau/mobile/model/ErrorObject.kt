package ru.andryss.trousseau.mobile.model

import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.ResponseBody
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.ItemStatus
import java.io.IOException

const val IO_EXCEPTION_ERROR_MESSAGE = "Произошла непредвиденная ошибка, повторите попытку позже"
const val RESPONSE_PARSE_ERROR_MESSAGE = "Произошла ошибка чтения, свяжитесь с поддержкой"
const val ERROR_RESPONSE_MESSAGE_TEMPLATE = "%s (код ошибки: %s)"

data class ErrorObject(
    val code: Int,
    val message: String,
    val humanMessage: String,
)

data class ItemMediaDto(
    val id: String,
    val href: String,
)

data class ItemDto(
    val id: String,
    val title: String?,
    val media: List<ItemMediaDto>,
    val description: String?,
    var status: ItemStatus,
)

val mapper = jacksonObjectMapper()

val callbackScope = CoroutineScope(Dispatchers.Main)

inline fun <reified T> callbackObj(
    crossinline onSuccess: (result: T) -> Unit,
    crossinline onError: (error: String) -> Unit,
) = commonCallbackObj(
    onSuccess = { response ->
        val result = mapper.readValue<T>(response.bytes())
        Log.i(TAG, "Got response $result")
        callbackScope.launch {
            onSuccess(result)
        }
    },
    onError = onError
)

inline fun noResponseCallbackObj(
    crossinline onSuccess: () -> Unit,
    crossinline onError: (error: String) -> Unit
) = commonCallbackObj(
    onSuccess = {
        callbackScope.launch {
            onSuccess()
        }
    },
    onError = onError
)

inline fun commonCallbackObj(
    crossinline onSuccess: (response: ResponseBody) -> Unit,
    crossinline onError: (error: String) -> Unit,
) = object : Callback {
    override fun onFailure(call: Call, e: IOException) {
        Log.e(TAG, "Error when sending request", e)
        callbackScope.launch {
            onError(IO_EXCEPTION_ERROR_MESSAGE)
        }
    }

    override fun onResponse(call: Call, response: Response) {
        if (response.code == 200) {
            response.body?.use {
                onSuccess(it)
            } ?: callbackScope.launch {
                onError(RESPONSE_PARSE_ERROR_MESSAGE)
            }
        } else {
            response.body?.use {
                val error = mapper.readValue<ErrorObject>(it.bytes())
                callbackScope.launch {
                    onError(ERROR_RESPONSE_MESSAGE_TEMPLATE.format(error.humanMessage, error.code))
                }
            } ?: callbackScope.launch {
                onError(RESPONSE_PARSE_ERROR_MESSAGE)
            }
        }
    }
}