package ru.andryss.trousseau.mobile.page

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.andryss.trousseau.mobile.AppState

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

@Composable
fun MainPage(state: AppState) {
    val navController = rememberNavController()
    state.navController = navController

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