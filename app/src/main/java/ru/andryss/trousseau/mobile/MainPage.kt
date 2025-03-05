package ru.andryss.trousseau.mobile

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun MainPage(state: AppState, navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "profile",
        builder = {
            composable("profile") {
                ProfilePage(state)
            }
            composable("items/create") {
                CreateItemPage()
            }
        }
    )
}