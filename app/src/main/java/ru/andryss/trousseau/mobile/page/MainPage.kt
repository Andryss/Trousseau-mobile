package ru.andryss.trousseau.mobile.page

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.andryss.trousseau.mobile.AppState

fun AppState.navigateSearchPage() {
    navController.navigate("search")
}

fun AppState.navigateItemPage(itemId: String) {
    navController.navigate("public/items/$itemId")
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
            composable("public/items/{itemId}") {
                val itemId = it.arguments?.getString("itemId")
                itemId?.let {
                    ItemPage(
                        state = state,
                        itemId = itemId
                    )
                }
            }
            composable("profile/{tab}") {
                val tab = it.arguments?.getString("tab")
                ProfileTab.fromPath(tab)?.let { selected ->
                    ProfilePage(state = state, selectedTab = selected)
                }
            }
            composable("seller/items/{itemId}") {
                val itemId = it.arguments?.getString("itemId")
                itemId?.let {
                    EditSellerItemPage(
                        state = state,
                        itemId = itemId
                    )
                }
            }
            composable("seller/items/{itemId}/preview") {
                val itemId = it.arguments?.getString("itemId")
                itemId?.let {
                    ItemPreviewPage(
                        state = state,
                        itemId = itemId
                    )
                }
            }
        }
    )
}