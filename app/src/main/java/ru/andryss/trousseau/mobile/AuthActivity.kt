package ru.andryss.trousseau.mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.andryss.trousseau.mobile.page.auth.MainAuthPage
import ru.andryss.trousseau.mobile.theme.TrousseauTheme

const val SHARED_PREF_NAME = "trousseau_preferences"
const val AUTH_TOKEN_KEY = "auth_token"

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val authToken = preferences.getString(AUTH_TOKEN_KEY, null)
        if (authToken != null) {
            switchToMainActivity()
        }

        val appState = application as AppState
        appState.configureWith(applicationContext)

        val onAuthSuccess = { token: String ->
            preferences.edit().putString(AUTH_TOKEN_KEY, token).apply()
            switchToMainActivity()
        }

        enableEdgeToEdge()
        setContent {
            TrousseauTheme {
                MainAuthPage(state = appState, onAuthSuccess = onAuthSuccess)
            }
        }
    }

    private fun switchToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}