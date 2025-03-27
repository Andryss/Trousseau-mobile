package ru.andryss.trousseau.mobile

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation.NavHostController
import okhttp3.OkHttpClient
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.page.MainPage
import ru.andryss.trousseau.mobile.theme.TrousseauTheme
import ru.andryss.trousseau.mobile.util.PropertyNames.Companion.TROUSSEAU_CONNECT_TIMEOUT
import ru.andryss.trousseau.mobile.util.PropertyNames.Companion.TROUSSEAU_REQUEST_TIMEOUT
import java.util.Properties
import java.util.concurrent.TimeUnit

const val TAG = "trousseau-mobile"

class AppCache {
    val feedItems = mutableStateListOf<ItemDto>()
}

class AppState : Application() {
    lateinit var properties: Properties
    lateinit var httpClient: OkHttpClient
    lateinit var navController: NavHostController
    lateinit var cache: AppCache
}

fun AppState.configureWith(applicationContext: Context) {
    properties = Properties().apply {
        load(applicationContext.assets.open("app.properties"))
    }

    httpClient = OkHttpClient.Builder()
        .connectTimeout(properties.getProperty(TROUSSEAU_CONNECT_TIMEOUT, "5").toLong(), TimeUnit.SECONDS)
        .callTimeout(properties.getProperty(TROUSSEAU_REQUEST_TIMEOUT, "10").toLong(), TimeUnit.SECONDS)
        .build()

    cache = AppCache()
}

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
