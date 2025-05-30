package ru.andryss.trousseau.mobile.page

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.andryss.trousseau.mobile.AUTH_TOKEN_KEY
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.AuthActivity
import ru.andryss.trousseau.mobile.SHARED_PREF_NAME
import ru.andryss.trousseau.mobile.client.auth.updateProfileInfo

fun AppState.navigateHomePage() {
    navController.navigate("home")
}

fun AppState.navigateSearchPage() {
    navController.navigate("search")
}

fun AppState.navigateItemPage(itemId: String, callback: ItemPageCallback) {
    navController.navigate("public/items/$itemId?callback=${callback.path}")
}

fun AppState.navigateProfilePage() {
    navController.navigate("profile")
}

fun AppState.navigateBookingsPage() {
    navController.navigate("bookings")
}

fun AppState.navigateSellerItemsPage() {
    navController.navigate("seller/items")
}

fun AppState.navigateSellerItemEditPage(itemId: String) {
    navController.navigate("seller/items/$itemId")
}

fun AppState.navigateSellerItemPreviewPage(itemId: String) {
    navController.navigate("seller/items/$itemId/preview")
}

fun AppState.navigateFavouritesPage() {
    navController.navigate("favourites")
}

fun AppState.navigateSubscriptionsPage() {
    navController.navigate("subscriptions")
}

fun AppState.navigateNotificationsPage() {
    navController.navigate("notifications")
}

fun AppState.signOut(localContext: Context) {
    if (localContext !is Activity) {
        throw IllegalArgumentException("context == LocalContext.current")
    }

    val preferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE)
    preferences.edit().remove(AUTH_TOKEN_KEY).apply()

    localContext.startActivity(Intent(this, AuthActivity::class.java))
    localContext.finish()
}

@Composable
fun MainPage(state: AppState) {
    val navController = rememberNavController()
    state.navController = navController

    LaunchedEffect(true) {
        state.updateProfileInfo()
    }

    NavHost(
        navController = navController,
        startDestination = "home",
        builder = {
            composable("home") {
                HomePage(state = state)
            }
            composable("search") {
                SearchPage(state = state)
            }
            composable("public/items/{itemId}?callback={callbackPage}") {
                val itemId = it.string("itemId")
                val callbackPage = ItemPageCallback.fromPath(it.string("callbackPage"))
                ItemPage(
                    state = state,
                    itemId = itemId,
                    callback = callbackPage
                )
            }
            composable("notifications") {
                NotificationsPage(state = state)
            }
            composable("favourites") {
                FavouritesPage(state = state)
            }
            composable("subscriptions") {
                SubscriptionsPage(state = state)
            }
            composable("profile") {
                ProfilePage(state = state)
            }
            composable("bookings") {
                MyBookingsPage(state = state)
            }
            composable("seller/items") {
                MyItemsPage(state = state)
            }
            composable("seller/items/{itemId}") {
                val itemId = it.string("itemId")
                EditSellerItemPage(
                    state = state,
                    itemId = itemId
                )
            }
            composable("seller/items/{itemId}/preview") {
                val itemId = it.string("itemId")
                ItemPreviewPage(
                    state = state,
                    itemId = itemId
                )
            }
        }
    )
}

private fun NavBackStackEntry.string(key: String) =
    arguments?.getString(key)
        ?: throw IllegalStateException("$key is not in arguments")