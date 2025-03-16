package ru.andryss.trousseau.mobile.page

import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.andryss.trousseau.mobile.AppState

fun AppState.navigateSearchPage() {
    navController.navigate("search")
}

fun AppState.navigateItemPage(itemId: String, callback: ItemPageCallback) {
    navController.navigate("public/items/$itemId?callback=${callback.path}")
}

fun AppState.navigateProfileBookingsPage() {
    navController.navigate("profile/bookings")
}

fun AppState.navigateProfileItemsPage() {
    navController.navigate("profile/items")
}

fun AppState.navigateSellerItemEditPage(itemId: String) {
    navController.navigate("seller/items/$itemId")
}

fun AppState.navigateSellerItemPreviewPage(itemId: String) {
    navController.navigate("seller/items/$itemId/preview")
}

@Composable
fun MainPage(state: AppState) {
    val navController = rememberNavController()
    state.navController = navController

    NavHost(
        navController = navController,
        startDestination = "search",
        builder = {
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
            composable("profile/{tab}") {
                val tab = ProfileTab.fromPath(it.string("tab"))
                ProfilePage(
                    state = state,
                    selectedTab = tab
                )
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