package ru.andryss.trousseau.mobile.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.ItemDto
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.pub.getFavourites
import ru.andryss.trousseau.mobile.util.replaceAllFrom
import ru.andryss.trousseau.mobile.widget.AlertDialogWrapper
import ru.andryss.trousseau.mobile.widget.BottomNavigationBar
import ru.andryss.trousseau.mobile.widget.BottomPage
import ru.andryss.trousseau.mobile.widget.ItemCard
import ru.andryss.trousseau.mobile.widget.ReturnBackTopBar

@Composable
fun FavouritesPage(state: AppState) {

    var getFavouritesLoading by remember { mutableStateOf(false) }

    val favourites = remember { mutableStateListOf<ItemDto>() }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun doGetFavourite() {
        getFavouritesLoading = true
        state.getFavourites(
            onSuccess = { items ->
                favourites.replaceAllFrom(items)
                getFavouritesLoading = false
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert = true
                getFavouritesLoading = false
            }
        )
    }

    LaunchedEffect(true) {
        doGetFavourite()
    }

    AlertDialogWrapper(
        isShown = showAlert,
        onDismiss = { showAlert = false },
        text = alertText
    ) {
        Scaffold(
            topBar = {
                ReturnBackTopBar(
                    title = "Избранное",
                    onReturn = { state.navigateProfilePage() }
                )
            },
            bottomBar = { BottomNavigationBar(state, BottomPage.PROFILE) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (getFavouritesLoading) {
                        CircularProgressIndicator()
                    } else {
                        if (favourites.isEmpty()) {
                            Text(
                                text = "В избранном пока пусто...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QuestionMark,
                                    contentDescription = null
                                )
                                Text(
                                    text = buildAnnotatedString {
                                        appendLine("Для добавления в избранное - нажмите")
                                        append("на ")
                                        appendInlineContent(id = "heart", alternateText = "❤")
                                        append(" в левом верхнем углу объявления")
                                    },
                                    inlineContent = mapOf(
                                        "heart" to InlineTextContent(
                                            placeholder = Placeholder(
                                                width = 20.sp,
                                                height = 20.sp,
                                                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.FavoriteBorder,
                                                contentDescription = "Favorite"
                                            )
                                        }
                                    ),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        } else {
                            for (item in favourites) {
                                ItemCard(state, item, ItemPageCallback.FAVOURITES)
                            }
                        }
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }
    }
}