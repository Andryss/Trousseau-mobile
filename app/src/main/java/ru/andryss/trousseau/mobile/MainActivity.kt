package ru.andryss.trousseau.mobile

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import okhttp3.OkHttpClient
import ru.andryss.trousseau.mobile.theme.TrousseauTheme
import ru.andryss.trousseau.mobile.util.PropertyNames.Companion.TROUSSEAU_REQUEST_TIMEOUT
import java.util.Properties
import java.util.concurrent.TimeUnit

const val TAG = "trousseau-mobile"

class AppState : Application() {
    lateinit var properties: Properties
    lateinit var httpClient: OkHttpClient
}

fun AppState.configureWith(applicationContext: Context) {
    properties = Properties().apply {
        load(applicationContext.assets.open("app.properties"))
    }

    httpClient = OkHttpClient.Builder()
        .callTimeout(properties.getProperty(TROUSSEAU_REQUEST_TIMEOUT, "30").toLong(), TimeUnit.SECONDS)
        .build()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appState = application as AppState
        appState.configureWith(applicationContext)

        enableEdgeToEdge()
        setContent {
            TrousseauTheme {
                MainPage(state = appState, navController = rememberNavController())
            }
        }
    }
}
