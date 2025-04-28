package ru.andryss.trousseau.mobile.widget

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.formatError
import ru.andryss.trousseau.mobile.client.pub.CategoryNode
import ru.andryss.trousseau.mobile.client.pub.getCategoryTree

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelectorModal(
    state: AppState,
    onSelect: (List<CategoryNode>) -> Unit,
    isSingleSelect: Boolean,
    onDismiss: () -> Unit
) {

    var getCategoryTreeLoading by remember { mutableStateOf(false) }
    
    var root by remember { mutableStateOf(CategoryNode.EMPTY) }

    val selected = remember { mutableStateListOf<CategoryNode>() }

    var showAlert by remember { mutableStateOf(false) }
    var alertText by remember { mutableStateOf("") }

    fun onSelect(category: CategoryNode) {
        if (category in selected) {
            selected.remove(category)
        } else {
            if (isSingleSelect) {
                selected.clear()
            }
            selected.add(category)
        }
    }
    
    LaunchedEffect(true) {
        getCategoryTreeLoading = true
        state.getCategoryTree(
            onSuccess = { rootNode ->
                root = rootNode
                getCategoryTreeLoading = false
            },
            onError = { error ->
                alertText = formatError(error)
                showAlert = true
                getCategoryTreeLoading = false
            }
        )
    }

    AlertDialogWrapper(
        isShown = showAlert,
        onDismiss = { showAlert = false },
        text = alertText
    ) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = rememberModalBottomSheetState(
                skipPartiallyExpanded = true
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (getCategoryTreeLoading) {
                    CircularProgressIndicator()
                } else {
                    CategoryNodeRootSelector(
                        category = root,
                        selected = selected,
                        onSelect = { onSelect(it) }
                    )
                    Button(
                        onClick = {
                            onSelect(selected)
                            onDismiss()
                        },
                        enabled = selected.isNotEmpty()
                    ) {
                        Text("Выбрать")
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryNodeRootSelector(
    category: CategoryNode,
    selected: List<CategoryNode>,
    onSelect: (CategoryNode) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.titleLarge
        )
        Column(
            modifier = Modifier
                .height(500.dp)
                .verticalScroll(rememberScrollState())
        ) {
            category.children.forEach { child ->
                CategoryNodeSelector(child, selected, onSelect)
            }
        }
    }
}

@Composable
fun CategoryNodeSelector(
    category: CategoryNode,
    selected: List<CategoryNode>,
    onSelect: (CategoryNode) -> Unit
) {

    var isExpanded by remember { mutableStateOf(false) }
    val hasChildren = remember { category.children.isNotEmpty() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (hasChildren) isExpanded = !isExpanded
                    else onSelect(category)
                },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.run {
                            if (!hasChildren) Icons.AutoMirrored.Filled.ArrowRight
                            else if (isExpanded) ExpandLess else ExpandMore
                        },
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            if (category in selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }
        if (isExpanded) {
            Column(
                modifier = Modifier.padding(start = 40.dp)
            ) {
                category.children.forEach { child ->
                    CategoryNodeSelector(child, selected, onSelect)
                }
            }
        }
    }
}

@Composable
@Preview
fun CategoryNodePreview() {

    val selected = remember { mutableStateListOf<CategoryNode>() }
    val isSingleSelect = false

    fun onSelect(category: CategoryNode) {
        if (category in selected) {
            selected.remove(category)
        } else {
            if (isSingleSelect) {
                selected.clear()
            }
            selected.add(category)
        }
    }

    Column {
        Spacer(modifier = Modifier.height(50.dp))
        CategoryNodeRootSelector(
            CategoryNode("all", "Все категории", listOf(
                CategoryNode("clothes", "Одежда и обувь", listOf(
                    CategoryNode("shoes1", "Обувь"),
                    CategoryNode("shoes2", "Одежда"),
                    CategoryNode("shoes3", "Кросовки"),
                    CategoryNode("shoes4", "Детские шорты"),
                )),
                CategoryNode("clothes", "Одежда и обувь", listOf(
                    CategoryNode("shoes5", "Обувь"),
                    CategoryNode("shoes6", "Одежда"),
                    CategoryNode("shoes7", "Кросовки"),
                    CategoryNode("shoes8", "Детские шорты"),
                ))
            )),
            selected = selected,
            onSelect = { onSelect(it) }
        )
    }
}