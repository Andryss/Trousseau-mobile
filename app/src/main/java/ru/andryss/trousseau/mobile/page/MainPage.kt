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

fun AppState.navigateProfilePage() {
    navController.navigate("profile")
}

fun AppState.navigateSellerItemEditPage(itemId: String) {
    navController.navigate("seller/items/$itemId")
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
            composable("profile") {
                ProfilePage(state = state)
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
        }
    )
}