package ru.andryss.trousseau.mobile

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

fun AppState.navigateSearchPage() {
    navController.navigate("search")
}

fun AppState.navigateProfilePage() {
    navController.navigate("profile")
}

fun AppState.navigateItemEditPage(itemId: String) {
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
            composable("profile") {
                ProfilePage(state = state)
            }
            composable("seller/items/{itemId}") {
                val itemId = it.arguments?.getString("itemId")
                itemId?.let {
                    EditItemPage(
                        state = state,
                        itemId = itemId
                    )
                }
            }
        }
    )
}