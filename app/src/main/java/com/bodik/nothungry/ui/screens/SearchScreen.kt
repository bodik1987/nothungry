package com.bodik.nothungry.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bodik.nothungry.data.CaloriesViewModel
import com.bodik.nothungry.data.Product
import com.bodik.nothungry.ui.components.AddIconButton
import com.bodik.nothungry.ui.components.CaloriesDialog
import com.bodik.nothungry.ui.components.CaloriesTopBar
import com.bodik.nothungry.ui.components.PlainTextField
import com.bodik.nothungry.ui.theme.RADIUS_OUTER
import com.bodik.nothungry.ui.theme.getGroupedShape

// --- Переиспользуемые компоненты ---

@Composable
fun NumberInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester? = null,
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(RADIUS_OUTER)
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
            modifier = Modifier
                .fillMaxWidth()
                .then(if (focusRequester != null) Modifier.focusRequester(focusRequester) else Modifier),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = TextStyle(
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    )
                }
                innerTextField()
            }
        )
    }
}

// --- SearchScreen ---

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: CaloriesViewModel,
    mealId: String,
    onBack: () -> Unit,
) {
    val productsCache = viewModel.productsCache
    val currentMeal = viewModel.meals.find { it.id == mealId }
    val mealName = currentMeal?.name ?: "Приём пищи"
    val mealCalories = currentMeal?.totalCalories ?: 0
    val totalCalories = viewModel.meals.sumOf { it.totalCalories }
    val dailyNorm = viewModel.dailyNorm

    var searchQuery by remember { mutableStateOf("") }
    var pendingProduct by remember { mutableStateOf<Product?>(null) }
    var weightInput by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editCals by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            CaloriesTopBar(
                totalCalories = totalCalories,
                dailyNorm = dailyNorm,
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(42.dp),
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                bottomContent = {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Поиск") },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { searchQuery = "" }) {
                                        Icon(Icons.Default.Clear, null)
                                    }
                                }
                            },
                            shape = RoundedCornerShape(RADIUS_OUTER),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            )
                        )
                        AddIconButton(
                            onClick = {
                                editTitle = searchQuery
                                editCals = ""
                                editDescription = ""
                                productToEdit = null
                                showEditDialog = true
                            }
                        )
                    }

                    Surface(
                        modifier = Modifier.padding(top = 16.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(RADIUS_OUTER)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "$mealName: $mealCalories ккал",
                                style = MaterialTheme.typography.labelLarge,
                                color = if (mealCalories > 500) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        val filtered = productsCache
            .filter { p ->
                p.title.contains(searchQuery, ignoreCase = true) ||
                        (p.description?.contains(searchQuery, ignoreCase = true) == true)
            }
            .sortedBy { it.title.lowercase() }

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 0.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(items = filtered, key = { _, p -> p.id ?: p.title }) { index, product ->
                Surface(
                    shape = getGroupedShape(index, filtered.size),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ListItem(
                        modifier = Modifier
                            .combinedClickable(
                                onClick = { pendingProduct = product; weightInput = "" },
                                onLongClick = {
                                    productToEdit = product
                                    editTitle = product.title
                                    editCals = product.calories.toString()
                                    editDescription = product.description ?: ""
                                    showEditDialog = true
                                }
                            )
                            .padding(2.dp),
                        headlineContent = {
                            Text(
                                product.title,
                                fontSize = 18.sp,
                                color = if (product.isDanger) MaterialTheme.colorScheme.error else Color.Unspecified
                            )
                        },
                        supportingContent = {
                            Column {
                                if (!product.description.isNullOrEmpty()) {
                                    Text(
                                        product.description,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                                Text(
                                    "${product.calories} ккал",
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }
            }
        }
    }

    // --- Диалог добавления порции ---
    val weightFocusRequester = remember { FocusRequester() }
    if (pendingProduct != null) {
        LaunchedEffect(pendingProduct) { weightFocusRequester.requestFocus() }

        // Рассчитываем калории для текущего ввода веса
        val currentWeight = weightInput.toIntOrNull() ?: 0
        val calculatedCalories = if (currentWeight > 0) {
            (pendingProduct!!.calories * currentWeight) / 100
        } else 0

        ModalBottomSheet(
            onDismissRequest = { pendingProduct = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
                    .navigationBarsPadding(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val titleText = remember(pendingProduct, calculatedCalories) {
                    val name = pendingProduct?.title ?: ""
                    if (calculatedCalories > 0) "$name - $calculatedCalories ккал" else name
                }

                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )

                if (!pendingProduct?.description.isNullOrBlank()) {
                    Text(
                        pendingProduct!!.description!!,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    NumberInputBox(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        placeholder = "Вес (г)",
                        modifier = Modifier.weight(0.45f), // Немного уменьшил, чтобы кнопка была больше
                        focusRequester = weightFocusRequester
                    )
                    Button(
                        onClick = {
                            val w = weightInput.toIntOrNull() ?: 0
                            if (w > 0) {
                                viewModel.addProductToMeal(
                                    mealId,
                                    pendingProduct!!.copy(
                                        id = pendingProduct!!.id + "_" + System.currentTimeMillis(),
                                        weight = w
                                    )
                                )
                                pendingProduct = null
                                searchQuery = ""
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(RADIUS_OUTER),
                    ) {
                        Text("Добавить")
                    }
                }
            }
        }
    }

    // --- Диалог создания/редактирования продукта ---
    if (showEditDialog) {
        var editIsDanger by remember { mutableStateOf(productToEdit?.isDanger ?: false) }

        CaloriesDialog(onDismiss = { showEditDialog = false }) {
            PlainTextField(
                value = editTitle,
                onValueChange = { editTitle = it },
                placeholder = "Название",
                focusRequester = weightFocusRequester
            )
            PlainTextField(
                value = editDescription,
                onValueChange = { editDescription = it },
                placeholder = "Описание",
                fontSize = 16,
                color = { MaterialTheme.colorScheme.outline }
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Пометить как опасный")
                Spacer(Modifier.weight(1f))
                androidx.compose.material3.Switch(
                    checked = editIsDanger,
                    onCheckedChange = { editIsDanger = it }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                NumberInputBox(
                    value = editCals,
                    onValueChange = { editCals = it },
                    placeholder = "Ккал",
                    modifier = Modifier.weight(0.65f)
                )
                Button(
                    onClick = {
                        val p = Product(
                            id = productToEdit?.id,
                            title = editTitle,
                            calories = editCals.toIntOrNull() ?: 0,
                            description = editDescription,
                            isDanger = editIsDanger
                        )
                        if (productToEdit == null) viewModel.addProduct(p)
                        else viewModel.updateProduct(p)
                        showEditDialog = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(RADIUS_OUTER),
                ) { Text("Сохранить") }
            }

            if (productToEdit != null) {
                Button(
                    onClick = { viewModel.deleteProduct(productToEdit!!); showEditDialog = false },
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(RADIUS_OUTER),
                ) { Text("Удалить") }
            }
        }
    }
}