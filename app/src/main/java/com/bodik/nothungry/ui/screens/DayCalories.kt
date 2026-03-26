package com.bodik.nothungry.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bodik.nothungry.data.CaloriesViewModel
import com.bodik.nothungry.data.Meal
import com.bodik.nothungry.data.Product
import com.bodik.nothungry.ui.components.ButtonGroup
import com.bodik.nothungry.ui.components.ButtonGroupItem
import com.bodik.nothungry.ui.components.CaloriesDialog
import com.bodik.nothungry.ui.components.CaloriesTopBar
import com.bodik.nothungry.ui.components.PlainTextField
import com.bodik.nothungry.ui.theme.DEFAULT_SPACER
import com.bodik.nothungry.ui.theme.ITEM_SPACING
import com.bodik.nothungry.ui.theme.RADIUS_OUTER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayCalories(
    viewModel: CaloriesViewModel,
    onNavigateToSearch: (mealId: String) -> Unit,
) {
    val meals = viewModel.meals

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showAddMealDialog by remember { mutableStateOf(false) }
    var mealToEdit by remember { mutableStateOf<Meal?>(null) }       // редактирование продукта внутри приёма
    var itemToEdit by remember { mutableStateOf<Product?>(null) }
    var editingMealId by remember { mutableStateOf<String?>(null) }   // какой приём редактируем продукт
    var weightInput by remember { mutableStateOf("") }
    var additionalWeight by remember { mutableStateOf("") }

    val dailyNorm = viewModel.dailyNorm
    val totalCalories = meals.sumOf { it.totalCalories }
    val restCalories = dailyNorm - totalCalories

    var isFabVisible by remember { mutableStateOf(true) }
    val listState = rememberLazyListState()
    val canScroll by remember { derivedStateOf { listState.canScrollForward || listState.canScrollBackward } }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (!canScroll) {
                    isFabVisible = true; return Offset.Zero
                }
                if (available.y < -5) isFabVisible = false
                if (available.y > 5) isFabVisible = true
                return Offset.Zero
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        topBar = {
            CaloriesTopBar(
                totalCalories = totalCalories,
                dailyNorm = dailyNorm,
                navigationIcon = {
                    IconButton(
                        onClick = { showSettingsSheet = true },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(42.dp),
                    ) {
                        Icon(Icons.Default.AccountCircle, null, modifier = Modifier.size(32.dp))
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = { showAddMealDialog = true },
                    shape = RoundedCornerShape(RADIUS_OUTER)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp))
                }
            }
        }
    ) { padding ->
        if (meals.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Здесь пока ничего нет", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(meals) { mealIndex, meal ->
                    MealCard(
                        meal = meal,
                        mealIndex = mealIndex,
                        mealsTotal = meals.size,
                        onAddProducts = { onNavigateToSearch(meal.id) },
                        onEditProduct = { product ->
                            itemToEdit = product
                            editingMealId = meal.id
                            weightInput = product.weight.toString()
                            additionalWeight = ""
                        }
                    )
                }
            }
        }
    }

    // --- Диалог добавления приёма пищи ---
    if (showAddMealDialog) {
        AddMealDialog(
            onDismiss = { showAddMealDialog = false },
            onConfirm = { name, navigateNow ->
                val meal = viewModel.addMeal(name)
                showAddMealDialog = false
                if (navigateNow) onNavigateToSearch(meal.id)
            }
        )
    }

    // --- Боттомшит настроек ---
    if (showSettingsSheet) {
        var tempWeight by remember { mutableFloatStateOf(viewModel.userWeight) }
        var tempAge by remember { mutableStateOf(viewModel.userAge.toString()) }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(DEFAULT_SPACER)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DEFAULT_SPACER / 2)
                ) {
                    LabeledBasicTextField(
                        value = tempAge,
                        onValueChange = { tempAge = it },
                        label = "Возраст",
                        modifier = Modifier.weight(1f)
                    )
                    LabeledBasicTextField(
                        value = tempWeight.toInt().toString(),
                        onValueChange = { tempWeight = it.toFloatOrNull() ?: tempWeight },
                        label = "Вес (кг)",
                        modifier = Modifier.weight(1f)
                    )
                }

                ButtonGroup(
                    items = listOf(
                        ButtonGroupItem(
                            "Очистить день",
                            { viewModel.clearDay(); showSettingsSheet = false }),
                        ButtonGroupItem("Сохранить", {
                            viewModel.saveUserSettings(
                                tempWeight,
                                tempAge.toIntOrNull() ?: viewModel.userAge
                            )
                            showSettingsSheet = false
                        }),
                    )
                )

                val uriHandler = LocalUriHandler.current
                Text(
                    text = "© Bohdan Shulika, 2026 · GitHub",
                    style = MaterialTheme.typography.bodySmall.copy(
                        textDecoration = TextDecoration.Underline
                    ),
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.clickable {
                        uriHandler.openUri("https://github.com/bodik1987")
                    }
                )
            }
        }
    }

    // --- Боттомшит редактирования продукта в приёме ---
    if (itemToEdit != null && editingMealId != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { itemToEdit = null; editingMealId = null },
            sheetState = sheetState,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(itemToEdit?.title ?: "", style = MaterialTheme.typography.titleLarge)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(DEFAULT_SPACER / 2)
                ) {
                    LabeledBasicTextField(
                        value = weightInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) weightInput = it },
                        label = "Вес (г)",
                        modifier = Modifier.weight(1f)
                    )
                    LabeledBasicTextField(
                        value = additionalWeight,
                        onValueChange = { if (it.all { c -> c.isDigit() }) additionalWeight = it },
                        label = "Добавить (+г)",
                        modifier = Modifier.weight(1f)
                    )
                }

                ButtonGroup(
                    items = listOf(
                        ButtonGroupItem("Удалить", {
                            viewModel.removeProductFromMeal(editingMealId!!, itemToEdit!!)
                            itemToEdit = null
                            editingMealId = null
                        }),
                        ButtonGroupItem("Обновить", {
                            val base = weightInput.toIntOrNull() ?: 0
                            val extra = additionalWeight.toIntOrNull() ?: 0
                            viewModel.updateProductInMeal(
                                editingMealId!!,
                                itemToEdit!!.copy(weight = base + extra)
                            )
                            itemToEdit = null
                            editingMealId = null
                        }),
                    )
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// MealCard — карточка одного приёма пищи
// ---------------------------------------------------------------------------

@Composable
private fun MealCard(
    meal: Meal,
    mealIndex: Int,
    mealsTotal: Int,
    onAddProducts: () -> Unit,
    onEditProduct: (Product) -> Unit,
) {
    Surface(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.8f)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .padding(start = 16.dp, end = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${meal.name} - ${meal.totalCalories} ккал",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Button(onClick = onAddProducts) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                }
            }

            // Список продуктов приёма
            if (meal.items.isNotEmpty()) {
                Column(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(ITEM_SPACING)
                ) {
                    meal.items.forEachIndexed { index, product ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = getGroupedShape(index, meal.items.size),
                            color = MaterialTheme.colorScheme.surfaceContainerLowest
                        ) {
                            ListItem(
                                modifier = Modifier
                                    .clickable { onEditProduct(product) }
                                    .padding(2.dp),
                                headlineContent = { Text(product.title, fontSize = 17.sp) },
                                supportingContent = {
                                    Text(
                                        "${product.weight} г",
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                },
                                trailingContent = {
                                    Text(
                                        "${(product.calories * product.weight) / 100}",
                                        fontSize = 17.sp
                                    )
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// AddMealDialog — диалог создания нового приёма пищи
// ---------------------------------------------------------------------------

@Composable
private fun AddMealDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, navigateNow: Boolean) -> Unit,
) {
    var mealName by remember { mutableStateOf("Приём пищи") }

    CaloriesDialog(onDismiss = onDismiss) {
        PlainTextField(
            value = mealName,
            onValueChange = { mealName = it },
            placeholder = "Название приёма",
            fontSize = 22,
        )

        Text(
            text = "Записывай всё, что ешь — даже маленькие перекусы. Чем точнее данные, тем лучше результат 🥗",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        Button(
            onClick = { onConfirm(mealName.ifBlank { "Приём пищи" }, true) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Добавить продукты")
        }
    }
}

// ---------------------------------------------------------------------------
// Утилиты (общие)
// ---------------------------------------------------------------------------

fun getGroupedShape(index: Int, total: Int): RoundedCornerShape {
    val l = 18.dp;
    val s = 2.dp
    return when {
        total == 1 -> RoundedCornerShape(l)
        index == 0 -> RoundedCornerShape(topStart = l, topEnd = l, bottomStart = s, bottomEnd = s)
        index == total - 1 -> RoundedCornerShape(
            topStart = s,
            topEnd = s,
            bottomStart = l,
            bottomEnd = l
        )

        else -> RoundedCornerShape(s)
    }
}

@Composable
fun LabeledBasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(RADIUS_OUTER / 2)
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            BasicTextField(
                value = value,
                onValueChange = { if (it.all { c -> c.isDigit() }) onValueChange(it) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                textStyle = TextStyle(
                    fontSize = 22.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                decorationBox = { innerTextField ->
                    if (value.isEmpty()) {
                        Text(
                            text = label,
                            style = TextStyle(
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}