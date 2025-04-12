package ru.andryss.trousseau.mobile

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation.NavHostController
import okhttp3.OkHttpClient
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.util.PropertyNames.Companion.TROUSSEAU_CONNECT_TIMEOUT
import ru.andryss.trousseau.mobile.util.PropertyNames.Companion.TROUSSEAU_REQUEST_TIMEOUT
import java.util.Properties
import java.util.concurrent.TimeUnit

class AppState : Application() {
    lateinit var properties: Properties
    lateinit var httpClient: OkHttpClient
    lateinit var navController: NavHostController
    lateinit var cache: AppCache
}

class AppCache {
    val homePageCache = HomePageCache()
}

class HomePageCache {
    val feedItems = mutableStateListOf<ItemDto>()
    var visibleItemIndex = 0
    var visibleItemOffset = 0
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