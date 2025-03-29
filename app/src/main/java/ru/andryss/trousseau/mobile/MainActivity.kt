package ru.andryss.trousseau.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.andryss.trousseau.mobile.page.MainPage
import ru.andryss.trousseau.mobile.theme.TrousseauTheme

const val TAG = "trousseau-mobile"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appState = application as AppState
        appState.configureWith(applicationContext)

        enableEdgeToEdge()
        setContent {
            TrousseauTheme {
                MainPage(state = appState)
            }
        }
    }
}
