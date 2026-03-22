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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.bodik.nothungry.data.Product
import com.bodik.nothungry.ui.components.ButtonGroup
import com.bodik.nothungry.ui.components.ButtonGroupItem
import com.bodik.nothungry.ui.theme.DEFAULT_SPACER
import com.bodik.nothungry.ui.theme.RADIUS_OUTER

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayCalories(
    viewModel: CaloriesViewModel,
    onNavigateToSearch: () -> Unit,
) {
    val selectedItems = viewModel.selectedItems

    var showSettingsSheet by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<Product?>(null) }
    var weightInput by remember { mutableStateOf("") }
    var additionalWeight by remember { mutableStateOf("") }

    val dailyNorm = viewModel.dailyNorm
    val totalCalories = selectedItems.sumOf { (it.calories * it.weight) / 100 }
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

    LaunchedEffect(selectedItems.toList()) { viewModel.saveDiary() }

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$totalCalories",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = " / $dailyNorm",
                            fontSize = 26.sp,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Text(
                            text = "ост. $restCalories",
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
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

                Spacer(modifier = Modifier.height(12.dp))

                val progressFactor = totalCalories.toFloat() / dailyNorm
                val progressColor = when {
                    progressFactor >= 1f -> MaterialTheme.colorScheme.error
                    progressFactor >= 0.8f -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.primary
                }
                LinearProgressIndicator(
                    progress = { progressFactor.coerceAtMost(1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(CircleShape),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = isFabVisible,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                FloatingActionButton(
                    onClick = onNavigateToSearch,
                    shape = RoundedCornerShape(RADIUS_OUTER)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp))
                }
            }
        }
    ) { padding ->
        if (selectedItems.isEmpty()) {
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
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                itemsIndexed(selectedItems) { index, product ->
                    Surface(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .fillMaxWidth(),
                        shape = getGroupedShape(index, selectedItems.size),
                        color = MaterialTheme.colorScheme.surfaceContainerLowest
                    ) {
                        ListItem(
                            modifier = Modifier
                                .clickable {
                                    itemToEdit = product
                                    weightInput = product.weight.toString()
                                    additionalWeight = ""
                                }
                                .padding(2.dp),
                            headlineContent = { Text(product.title, fontSize = 18.sp) },
                            supportingContent = {
                                Text("${product.weight} г", modifier = Modifier.padding(top = 4.dp))
                            },
                            trailingContent = {
                                Text(
                                    "${(product.calories * product.weight) / 100}",
                                    fontSize = 18.sp
                                )
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                        )
                    }
                }
            }
        }
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

    // --- Боттомшит редактирования позиции ---
    if (itemToEdit != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { itemToEdit = null },
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
                            selectedItems.remove(itemToEdit)
                            itemToEdit = null
                        }),
                        ButtonGroupItem("Обновить", {
                            val idx = selectedItems.indexOf(itemToEdit)
                            if (idx != -1) {
                                val base = weightInput.toIntOrNull() ?: 0
                                val extra = additionalWeight.toIntOrNull() ?: 0
                                selectedItems[idx] = itemToEdit!!.copy(weight = base + extra)
                            }
                            itemToEdit = null
                        }),
                    )
                )
            }
        }
    }
}

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