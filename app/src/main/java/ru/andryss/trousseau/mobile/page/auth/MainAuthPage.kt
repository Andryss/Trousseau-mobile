package ru.andryss.trousseau.mobile.page.auth

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.andryss.trousseau.mobile.AppState

fun AppState.navigateSignInPage() {
    navController.navigate("signIn")
}

fun AppState.navigateSignUpPage() {
    navController.navigate("signUp")
}

@Composable
fun MainAuthPage(state: AppState, onAuthSuccess: (String) -> Unit) {
    val navController = rememberNavController()
    state.navController = navController

    NavHost(
        navController = navController,
        startDestination = "signIn",
        builder = {
            composable("signIn") {
                SignInPage(state = state, onAuthSuccess = onAuthSuccess)
            }
            composable("signUp") {
                SignUpPage(state = state, onAuthSuccess = onAuthSuccess)
            }
        }
    )
}