package ru.andryss.trousseau.mobile.page.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity.MODE_PRIVATE
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.andryss.trousseau.mobile.AUTH_TOKEN_KEY
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.MainActivity
import ru.andryss.trousseau.mobile.SHARED_PREF_NAME

fun AppState.navigateSignInPage() {
    navController.navigate("signIn")
}

fun AppState.navigateSignUpPage() {
    navController.navigate("signUp")
}

fun AppState.signIn(localContext: Context, token: String) {
    if (localContext !is Activity) {
        throw IllegalArgumentException("context == LocalContext.current")
    }

    val preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
    preferences.edit().putString(AUTH_TOKEN_KEY, token).apply()

    localContext.startActivity(Intent(this, MainActivity::class.java))
    localContext.finish()
}

@Composable
fun MainAuthPage(state: AppState) {
    val navController = rememberNavController()
    state.navController = navController

    NavHost(
        navController = navController,
        startDestination = "signIn",
        builder = {
            composable("signIn") {
                SignInPage(state = state)
            }
            composable("signUp") {
                SignUpPage(state = state)
            }
        }
    )
}