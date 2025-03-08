package ru.andryss.trousseau.mobile

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

fun AppState.navigateItemEditPage(itemId: String) {
    navController.navigate("seller/items/$itemId")
}

fun AppState.navigateProfilePage() {
    navController.navigate("profile")
}

@Composable
fun MainPage(state: AppState) {
    val navController = rememberNavController()
    state.navController = navController

    NavHost(
        navController = navController,
        startDestination = "profile",
        builder = {
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