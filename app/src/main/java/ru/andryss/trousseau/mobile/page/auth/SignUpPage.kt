package ru.andryss.trousseau.mobile.page.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.auth.SignUpRequest
import ru.andryss.trousseau.mobile.client.auth.signUp
import ru.andryss.trousseau.mobile.widget.AlertWrapper

@Composable
fun SignUpPage(state: AppState, onAuthSuccess: (String) -> Unit) {

    var signUpLoading by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var isUsernameError by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf("") }
    var isPasswordError by remember { mutableStateOf(false) }

    val links = remember { mutableStateListOf("") }
    var isLinksError by remember { mutableStateOf(false) }

    var isExpandSellerFields by remember { mutableStateOf(false) }

    var room by remember { mutableStateOf("") }
    var isRoomError by remember { mutableStateOf(false) }

    val showAlert = remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun getUsername() =
        username.trim().lowercase()

    fun setUsername(value: String) {
        username = value
        isUsernameError = false
    }

    fun getPassword() =
        password.trim()

    fun setPassword(value: String) {
        password = value
        isPasswordError = false
    }

    fun getLinks() =
        links.map { it.trim() }.filter { it.isNotBlank() }

    fun getRoom() =
        room.trim().ifBlank { null }

    fun setRoom(value: String) {
        room = value
        isRoomError = false
    }

    fun onSignUp() {
        val usernameValue = getUsername()
        if (usernameValue.isBlank()) {
            isUsernameError = true
            return
        }
        isUsernameError = false

        val passwordValue = getPassword()
        if (passwordValue.isBlank()) {
            isPasswordError = true
            return
        }
        isPasswordError = false

        val linksValue = getLinks()
        if (linksValue.isEmpty() || linksValue.size != links.size) {
            isLinksError = true
            return
        }
        isLinksError = false

        val roomValue = getRoom()
        if (isExpandSellerFields && roomValue.isNullOrBlank()) {
            isRoomError = true
            return
        }
        isRoomError = false

        signUpLoading = true
        state.signUp(
            SignUpRequest(
                username = usernameValue,
                password = passwordValue,
                contacts = linksValue,
                room = roomValue
            ),
            onSuccess = { result ->
                onAuthSuccess(result)
                signUpLoading = false
            },
            onError = { error ->
                alertText = error
                showAlert.value = true
                signUpLoading = false
            }
        )
    }

    AlertWrapper(
        isShown = showAlert,
        text = alertText
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Регистрация",
                    style = MaterialTheme.typography.headlineSmall
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { setUsername(it) },
                    modifier = Modifier.width(320.dp),
                    label = { Text("Логин") },
                    isError = isUsernameError,
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { setPassword(it) },
                    modifier = Modifier.width(320.dp),
                    label = { Text("Пароль") },
                    isError = isPasswordError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    singleLine = true
                )
                LazyColumn {
                    itemsIndexed(links) { index, link ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            OutlinedTextField(
                                value = link,
                                onValueChange = { links[index] = it },
                                modifier = Modifier.width(320.dp),
                                label = {
                                    if (index == 0) {
                                        Text("Основной контакт для связи")
                                    } else {
                                        Text("Дополнительный контакт $index")
                                    }
                                },
                                isError = isLinksError && links[index].trim().isBlank(),
                                trailingIcon = {
                                    if (index != 0) {
                                        IconButton(
                                            onClick = { links.removeAt(index) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Remove contact link"
                                            )
                                        }
                                    }
                                },
                                singleLine = true
                            )
                        }
                    }
                }
                if (links.size < 5) {
                    OutlinedButton(
                        onClick = { links.add("") },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.run {
                                if (links.isEmpty() && isLinksError) error else primary
                            }
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add contact link"
                        )
                        Text("Добавить еще контакт")
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = null,
                            indication = null,
                            onClick = { isExpandSellerFields = !isExpandSellerFields }
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Checkbox(
                        checked = isExpandSellerFields,
                        onCheckedChange = { isExpandSellerFields = it }
                    )
                    Text(text = "Хочу размещать объявления")
                }
                if (isExpandSellerFields) {
                    OutlinedTextField(
                        value = room,
                        onValueChange = { setRoom(it) },
                        modifier = Modifier.width(320.dp),
                        label = { Text("Комната") },
                        isError = isRoomError,
                        singleLine = true
                    )
                }
                OutlinedButton(
                    onClick = { onSignUp() }
                ) {
                    Text(text = "Зарегистрироваться")
                }
                Text(
                    text = buildAnnotatedString {
                        append("Уже есть аккаунт? ")
                        val link = LinkAnnotation.Clickable(
                            tag = "sign in",
                            linkInteractionListener = { state.navigateSignInPage() }
                        )
                        withLink(link) {
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append("Войти")
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}