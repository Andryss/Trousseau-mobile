package ru.andryss.trousseau.mobile.client

import android.util.Log
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.ResponseBody
import ru.andryss.trousseau.mobile.TAG
import ru.andryss.trousseau.mobile.util.ItemStatus
import ru.andryss.trousseau.mobile.util.Strings
import java.io.IOException
import java.time.OffsetDateTime
import java.util.Random

const val IO_EXCEPTION_ERROR_MESSAGE = "Произошла непредвиденная ошибка, повторите попытку позже"
const val RESPONSE_PARSE_ERROR_MESSAGE = "Произошла ошибка чтения, свяжитесь с поддержкой"
const val ERROR_RESPONSE_MESSAGE_TEMPLATE = "%s (код ошибки: %s)"

data class ErrorObject(
    val code: Int,
    val message: String,
    val humanMessage: String,
)

data class AuthorDto(
    val username: String,
    val contacts: List<String>,
    val room: String? = null,
) {
    companion object {
        val EMPTY = AuthorDto("", listOf())
    }
}

data class ItemMediaDto(
    val id: String,
    val href: String,
)

data class CategoryDto(
    val id: String,
    val name: String
)

data class ItemDto(
    val id: String,
    val author: AuthorDto,
    var status: ItemStatus,
    val title: String? = null,
    val media: List<ItemMediaDto> = listOf(),
    val description: String? = null,
    val category: CategoryDto? = null,
    val cost: Long? = null,
    var isFavourite: Boolean = false,
    val publishedAt: OffsetDateTime? = null,
) {
    companion object {
        val EMPTY = ItemDto("", AuthorDto.EMPTY, ItemStatus.UNKNOWN)
    }
    fun formatCost(): String {
        if (cost == null || cost == 0L) {
            return Strings.EMPTY_ITEM_COST
        }
        return "$cost \u20BD"
    }
}

data class ItemListResponse(
    val items: List<ItemDto>,
)

val mapper = jacksonObjectMapper().apply {
    registerModule(JavaTimeModule())
}

val callbackScope = CoroutineScope(Dispatchers.Main)

inline fun <reified T> callbackObj(
    crossinline onSuccess: (result: T) -> Unit,
    crossinline onError: (error: ErrorObject) -> Unit,
) = commonCallbackObj(
    onSuccess = { response ->
        val result = mapper.readValue<T>(response.bytes())
        Log.i(TAG, "Got response $result")
        callbackScope.launch {
            uxDelay()
            onSuccess(result)
        }
    },
    onError = onError
)

inline fun noResponseCallbackObj(
    crossinline onSuccess: () -> Unit,
    crossinline onError: (error: ErrorObject) -> Unit
) = commonCallbackObj(
    onSuccess = {
        Log.i(TAG, "Got empty response")
        callbackScope.launch {
            uxDelay()
            onSuccess()
        }
    },
    onError = onError
)

suspend fun uxDelay() =
    delay((800 + 200 * Random().nextGaussian()).toLong())

inline fun commonCallbackObj(
    crossinline onSuccess: (response: ResponseBody) -> Unit,
    crossinline onError: (error: ErrorObject) -> Unit,
) = object : Callback {
    override fun onFailure(call: Call, e: IOException) {
        Log.e(TAG, "Error when sending request", e)
        callbackScope.launch {
            onError(
                ErrorObject(
                    code = 1,
                    message = "io.error",
                    humanMessage = IO_EXCEPTION_ERROR_MESSAGE
                )
            )
        }
    }

    override fun onResponse(call: Call, response: Response) {
        val body = response.body
        if (response.code == 200) {
            if (body == null) {
                callbackScope.launch {
                    onError(
                        ErrorObject(
                            code = 1,
                            message = "response.200.parse.error",
                            humanMessage = RESPONSE_PARSE_ERROR_MESSAGE
                        )
                    )
                }
            } else {
                body.use { onSuccess(it) }
            }
        } else if (body == null) {
            callbackScope.launch {
                onError(
                    ErrorObject(
                        code = 1,
                        message = "response.no200.parse.error",
                        humanMessage = RESPONSE_PARSE_ERROR_MESSAGE
                    )
                )
            }
        } else {
            body.use {
                try {
                    val error = mapper.readValue<ErrorObject>(it.bytes())
                    Log.i(TAG, "Got error object response $error")
                    callbackScope.launch {
                        onError(error)
                    }
                } catch (e: Exception) {
                    callbackScope.launch {
                        onError(
                            ErrorObject(
                                code = 1,
                                message = "response.invalid.body.error",
                                humanMessage = RESPONSE_PARSE_ERROR_MESSAGE
                            )
                        )
                    }
                }
            }
        }
    }
}

fun formatError(error: ErrorObject) =
    ERROR_RESPONSE_MESSAGE_TEMPLATE.format(error.humanMessage, error.code)