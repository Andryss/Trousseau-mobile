package ru.andryss.trousseau.mobile

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.andryss.trousseau.mobile.notification.configureNotificationWorker
import ru.andryss.trousseau.mobile.page.MainPage
import ru.andryss.trousseau.mobile.theme.TrousseauTheme

const val TAG = "trousseau-mobile"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
        val authToken = preferences.getString(AUTH_TOKEN_KEY, null)
        if (authToken == null) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        }

        val appState = application as AppState
        appState.configureWith(applicationContext)

        configureNotificationWorker()

        enableEdgeToEdge()
        setContent {
            TrousseauTheme {
                MainPage(state = appState)
            }
        }
    }
}
