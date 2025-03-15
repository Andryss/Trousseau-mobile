package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.widget.BottomBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.TopBar

enum class ProfileTab(
    val title: String
) {
    BOOKINGS("Мои бронирования"),
    ITEMS("Мои объявления")
}

@Composable
fun ProfilePage(state: AppState, selectedTab: ProfileTab = ProfileTab.BOOKINGS) {

    var selectedIndex by remember { mutableIntStateOf(selectedTab.ordinal) }

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomBar(state, BottomPage.PROFILE) },
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                TabRow(
                    selectedTabIndex = selectedIndex
                ) {
                    ProfileTab.entries.forEachIndexed { index, tab ->
                        Tab(
                            selected = selectedIndex == index,
                            onClick = { selectedIndex = index },
                            text = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        )
                    }
                }
                when (selectedIndex) {
                    0 -> MyBookingsSubpage(state)
                    1 -> MyItemsSubpage(state)
                }
            }
        }
    }
}