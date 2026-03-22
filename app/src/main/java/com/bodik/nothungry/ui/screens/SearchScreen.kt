package com.bodik.nothungry.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchScreen(
    viewModel: CaloriesViewModel,
    onBack: () -> Unit,
) {
    val productsCache = viewModel.productsCache
    val selectedItems = viewModel.selectedItems

    var searchQuery by remember { mutableStateOf("") }
    var pendingProduct by remember { mutableStateOf<Product?>(null) }
    var weightInput by remember { mutableStateOf("") }

    var showEditDialog by remember { mutableStateOf(false) }
    var productToEdit by remember { mutableStateOf<Product?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editCals by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }

    val searchFocusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { searchFocusRequester.requestFocus() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        topBar = {
            Column(modifier = Modifier.padding(top = 40.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(42.dp),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp)
                            .height(56.dp)
                            .focusRequester(searchFocusRequester),
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, "Очистить")
                                }
                            }
                        },
                        shape = CircleShape,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )

                    IconButton(
                        onClick = {
                            editTitle = searchQuery
                            editCals = ""
                            editDescription = ""
                            productToEdit = null
                            showEditDialog = true
                        },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.size(42.dp),
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp))
                    }
                }
            }
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
            contentPadding = PaddingValues(10.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(items = filtered, key = { _, p -> p.id ?: p.title }) { index, product ->
                val shape = getGroupedShape(index, filtered.size)
                Surface(
                    shape = shape,
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
                        headlineContent = { Text(product.title, fontSize = 18.sp) },
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
        AlertDialog(
            onDismissRequest = { pendingProduct = null },
            title = {
                Column {
                    Text(pendingProduct?.title ?: "")
                    if (!pendingProduct?.description.isNullOrBlank()) {
                        Text(
                            pendingProduct!!.description!!,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { if (it.all { c -> c.isDigit() }) weightInput = it },
                        label = { Text("Вес (г)") },
                        textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(0.65f)
                            .focusRequester(weightFocusRequester)
                    )
                    Button(
                        onClick = {
                            val w = weightInput.toIntOrNull() ?: 0
                            if (w > 0) {
                                val idx =
                                    selectedItems.indexOfFirst { it.title == pendingProduct?.title }
                                if (idx != -1) selectedItems[idx] =
                                    selectedItems[idx].copy(weight = selectedItems[idx].weight + w)
                                else selectedItems.add(pendingProduct!!.copy(weight = w))
                                pendingProduct = null
                                searchQuery = ""
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Добавить") }
                }
            }
        )
    }

    // --- Диалог создания/редактирования продукта ---
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(if (productToEdit == null) "Новый продукт" else "Редактировать") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = editTitle,
                        onValueChange = { editTitle = it },
                        label = { Text("Название") },
                        textStyle = TextStyle(fontSize = 18.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editDescription,
                        onValueChange = { editDescription = it },
                        label = { Text("Описание") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            dismissButton = {
                if (productToEdit != null) {
                    Button(
                        onClick = {
                            viewModel.deleteProduct(productToEdit!!)
                            showEditDialog = false
                        },
                        modifier = Modifier.offset(y = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) { Text("Удалить") }
                }
            },
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = editCals,
                        onValueChange = { if (it.all { c -> c.isDigit() }) editCals = it },
                        label = { Text("Ккал", textAlign = TextAlign.Center) },
                        textStyle = TextStyle(fontSize = 20.sp, textAlign = TextAlign.Center),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.65f)
                    )
                    Button(
                        onClick = {
                            val p = Product(
                                id = productToEdit?.id,
                                title = editTitle,
                                calories = editCals.toIntOrNull() ?: 0,
                                description = editDescription
                            )
                            if (productToEdit == null) viewModel.addProduct(p)
                            else viewModel.updateProduct(p)
                            showEditDialog = false
                        },
                        modifier = Modifier.weight(1f)
                    ) { Text("Сохранить") }
                }
            }
        )
    }
}