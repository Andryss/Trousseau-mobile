package ru.andryss.trousseau.mobile.widget

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.andryss.trousseau.mobile.AppState
import ru.andryss.trousseau.mobile.client.pub.CategoryNode
import ru.andryss.trousseau.mobile.client.pub.subscriptions.SubscriptionData
import ru.andryss.trousseau.mobile.client.pub.subscriptions.SubscriptionDto
import ru.andryss.trousseau.mobile.client.pub.subscriptions.SubscriptionInfoRequest
import ru.andryss.trousseau.mobile.util.replaceAllFrom

@Composable
fun SubscriptionForm(
    state: AppState,
    reference: SubscriptionDto? = null,
    onSubmit: (SubscriptionInfoRequest) -> Unit,
    onCancel: () -> Unit
) {

    var newSubName by remember { mutableStateOf(reference?.name ?: "") }
    var newSubNameError by remember { mutableStateOf(false) }

    val newSubCategories = remember { mutableStateListOf<CategoryNode>().apply {
        reference?.let { ref ->
            addAll(ref.data.categories.map { CategoryNode(it.id, it.name) })
        }
    } }
    var newSubCategoriesText by remember { mutableStateOf(
        reference?.let { ref ->
            ref.data.categories.joinToString(
                separator = ",\n",
                transform = { it.name }
            )
        } ?: ""
    ) }
    var newSubCategoriesError by remember { mutableStateOf(false) }

    var showCategoryModal by remember { mutableStateOf(false) }

    fun getName() =
        newSubName.trim()

    fun getCategories() =
        newSubCategories.map { it.id }

    fun onEditFinish() {
        if (getName().isBlank()) {
            newSubNameError = true
            return
        }
        newSubNameError = false

        if (newSubCategories.isEmpty()) {
            newSubCategoriesError = true
            return
        }
        newSubCategoriesError = false

        val result = SubscriptionInfoRequest(
            name = getName(),
            data = SubscriptionData(
                categoryIds = getCategories()
            )
        )
        onSubmit(result)
    }

    fun onEditCancel() {
        onCancel()
    }

    fun onNameChange(value: String) {
        newSubName = value
        newSubNameError = false
    }

    fun onCategoriesSelect(nodes: List<CategoryNode>) {
        newSubCategoriesText = nodes.joinToString(
            separator = ",\n",
            transform = { it.name }
        )
        newSubCategories.replaceAllFrom(nodes)
        newSubCategoriesError = false
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = newSubName,
            onValueChange = { onNameChange(it) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Название") },
            singleLine = true,
            isError = newSubNameError
        )
        OutlinedTextField(
            value = newSubCategoriesText,
            onValueChange = { },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Категории") },
            readOnly = true,
            isError = newSubCategoriesError,
            interactionSource = remember { MutableInteractionSource() }
                .also { interactionSource ->
                    LaunchedEffect(interactionSource) {
                        interactionSource.interactions.collect {
                            if (it is PressInteraction.Release) {
                                showCategoryModal = true
                            }
                        }
                    }
                }
        )
        Row(
            modifier = Modifier.padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { onEditFinish() }
            ) {
                Text("Сохранить")
            }
            OutlinedButton(
                onClick = { onEditCancel() }
            ) {
                Text("Отменить")
            }
        }

        if (showCategoryModal) {
            CategorySelectorModal(
                state = state,
                onSelect = { onCategoriesSelect(it) },
                isSingleSelect = false,
                onDismiss = { showCategoryModal = false }
            )
        }
    }
}