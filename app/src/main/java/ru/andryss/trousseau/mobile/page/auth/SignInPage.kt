package ru.andryss.trousseau.mobile.page.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.auth.SignInRequest
import ru.andryss.trousseau.mobile.client.auth.signIn
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.widget.AlertDialogWrapper

@Composable
fun SignInPage(state: AppState) {

    val context = LocalContext.current

    var signInLoading by remember { mutableStateOf(false) }

    var username by remember { mutableStateOf("") }
    var isUsernameError by remember { mutableStateOf(false) }

    var password by remember { mutableStateOf("") }
    var isPasswordError by remember { mutableStateOf(false) }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun getUsername() =
        username.trim().lowercase()

    fun getPassword() =
        password.trim()

    fun onSignIn() {
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

        signInLoading = true
        state.signIn(
            SignInRequest(
                username = usernameValue,
                password = passwordValue
            ),
            onSuccess = { result ->
                state.signIn(context, result)
                signInLoading = false
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert = true
                signInLoading = false
            }
        )
    }

    AlertDialogWrapper(
        isShown = showAlert,
        onDismiss = { showAlert = false },
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
                    text = "Вход",
                    style = MaterialTheme.typography.headlineSmall
                )
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.width(320.dp),
                    label = { Text("Логин") },
                    isError = isUsernameError,
                    singleLine = true
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.width(320.dp),
                    label = { Text("Пароль") },
                    isError = isPasswordError,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    singleLine = true
                )
                OutlinedButton(
                    onClick = { onSignIn() }
                ) {
                    Text(text = "Войти")
                }
                Text(
                    text = buildAnnotatedString {
                        append("Еще нет аккаунта? ")
                        val link = LinkAnnotation.Clickable(
                            tag = "sign up",
                            linkInteractionListener = { state.navigateSignUpPage() }
                        )
                        withLink(link) {
                            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                                append("Зарегистрироваться")
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}