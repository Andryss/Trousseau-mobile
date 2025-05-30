package ru.andryss.trousseau.mobile.widget

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.page.navigateHomePage
import ru.andryss.trousseau.mobile.page.navigateProfilePage
import ru.andryss.trousseau.mobile.page.navigateSearchPage

enum class BottomPage(
    val icon: ImageVector,
    val text: String,
    val onClick: (AppState) -> Unit
) {
    HOME(
        Icons.Default.Home,
        "Главная",
        { it.navigateHomePage() }
    ),
    SEARCH(
        Icons.Default.Search,
        "Поиск",
        { it.navigateSearchPage() }
    ),
    PROFILE(
        Icons.Default.AccountCircle,
        "Профиль",
        { it.navigateProfilePage() }
    )
}

@Composable
fun BottomNavigationBar(state: AppState, page: BottomPage) {
    NavigationBar {
        BottomPage.entries.forEach { entry ->
            NavigationBarItem(
                icon = { Icon(entry.icon, null) },
                label = { Text(entry.text) },
                selected = (page == entry),
                onClick = {
                    if (page != entry) {
                        entry.onClick(state)
                    }
                }
            )
        }
    }
}