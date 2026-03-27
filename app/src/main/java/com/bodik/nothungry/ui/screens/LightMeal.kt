package com.bodik.nothungry.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bodik.nothungry.data.CaloriesViewModel
import com.bodik.nothungry.data.LightMealItem
import com.bodik.nothungry.ui.components.IslandColumn
import com.bodik.nothungry.ui.components.IslandListItem
import com.bodik.nothungry.ui.components.ScreenLayout
import com.bodik.nothungry.ui.theme.DEFAULT_SPACER
import com.bodik.nothungry.ui.theme.RADIUS_OUTER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightMeal(viewModel: CaloriesViewModel) {
    val meals = viewModel.lightMeals

    var showSheet by remember { mutableStateOf(false) }
    var editingMeal by remember { mutableStateOf<LightMealItem?>(null) }
    var deletingMeal by remember { mutableStateOf<LightMealItem?>(null) }
    var newMealName by remember { mutableStateOf("") }
    var newMealDesc by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    fun openAdd() {
        editingMeal = null
        newMealName = ""
        newMealDesc = ""
        showSheet = true
    }

    fun openEdit(meal: LightMealItem) {
        editingMeal = meal
        newMealName = meal.name
        newMealDesc = meal.description
        showSheet = true
    }

    fun save() {
        if (newMealName.isBlank()) return
        if (editingMeal != null) {
            viewModel.updateLightMeal(
                editingMeal!!.copy(
                    name = newMealName,
                    description = newMealDesc
                )
            )
        } else {
            viewModel.addLightMeal(newMealName, newMealDesc)
        }
        showSheet = false
    }

    fun delete(meal: LightMealItem) {
        viewModel.deleteLightMeal(meal)
        deletingMeal = null
    }

    ScreenLayout(
        title = "Лёгкий приём пищи",
    ) {
        IslandColumn(
            items = meals.map { meal ->
                IslandListItem(
                    id = meal.id.toString(),
                    label = meal.name,
                    supportingText = meal.description.ifBlank { null },
                    onClick = { openEdit(meal) }
                )
            }
        )

        Spacer(modifier = Modifier.height(DEFAULT_SPACER))

        Button(
            onClick = { openAdd() },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = DEFAULT_SPACER,
                vertical = DEFAULT_SPACER
            ),
            shape = RoundedCornerShape(RADIUS_OUTER),
        ) {
            Text("Добавить блюдо", style = MaterialTheme.typography.bodyLarge)
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = DEFAULT_SPACER)
                    .padding(bottom = DEFAULT_SPACER)
            ) {
                BasicTextField(
                    value = newMealName,
                    onValueChange = { newMealName = it },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    textStyle = TextStyle(
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        if (newMealName.isEmpty()) {
                            Text(
                                text = "Название",
                                style = TextStyle(
                                    fontSize = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(DEFAULT_SPACER))

                BasicTextField(
                    value = newMealDesc,
                    onValueChange = { newMealDesc = it },
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    decorationBox = { innerTextField ->
                        if (newMealDesc.isEmpty()) {
                            Text(
                                text = "Описание",
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            )
                        }
                        innerTextField()
                    }
                )

                Spacer(modifier = Modifier.height(DEFAULT_SPACER * 2))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (editingMeal != null) {
                        Button(
                            onClick = {
                                showSheet = false
                                deletingMeal = editingMeal
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            contentPadding = PaddingValues(DEFAULT_SPACER),
                            shape = RoundedCornerShape(RADIUS_OUTER),
                        ) { Text("Удалить") }
                    }

                    Button(
                        onClick = { save() },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(DEFAULT_SPACER),
                        shape = RoundedCornerShape(RADIUS_OUTER),
                    ) {
                        Text(
                            text = if (editingMeal != null) "Сохранить изменения" else "Сохранить",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }

    deletingMeal?.let { meal ->
        AlertDialog(
            onDismissRequest = { deletingMeal = null },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Удалить блюдо?",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Text(
                    text = "${meal.name} будет удалено из списка.",
                    color = MaterialTheme.colorScheme.secondary
                )
            },
            confirmButton = {
                TextButton(onClick = { delete(meal) }) {
                    Text(
                        text = "Удалить",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { deletingMeal = null }) {
                    Text(
                        text = "Отмена",
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        )
    }
}