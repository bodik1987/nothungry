package com.bodik.nothungry.data

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import kotlinx.serialization.json.Json

class CaloriesViewModel(app: Application) : AndroidViewModel(app) {

    private val diaryPrefs = app.getSharedPreferences("diary", Context.MODE_PRIVATE)
    private val userPrefs = app.getSharedPreferences("user_settings", Context.MODE_PRIVATE)

    /** Позиции дневника питания за день */
    val selectedItems = mutableStateListOf<Product>()

    /** Кэш продуктов для поиска (начинается с дефолтного списка) */
    val productsCache = mutableStateListOf<Product>()

    /** Настройки пользователя */
    var userWeight by mutableFloatStateOf(userPrefs.getFloat("weight", 82f))
        private set
    var userAge by mutableIntStateOf(userPrefs.getInt("age", 39))
        private set

    init {
        // Загружаем дневник
        diaryPrefs.getString("items", null)?.let { saved ->
            try {
                selectedItems.addAll(Json.decodeFromString<List<Product>>(saved))
            } catch (_: Exception) {
            }
        }

        // Загружаем пользовательские продукты; если нет — дефолтный список
        val savedProducts = userPrefs.getString("products", null)
        if (savedProducts != null) {
            try {
                productsCache.addAll(Json.decodeFromString<List<Product>>(savedProducts))
            } catch (_: Exception) {
                productsCache.addAll(DEFAULT_PRODUCTS)
            }
        } else {
            productsCache.addAll(DEFAULT_PRODUCTS)
        }
    }

    // ---------- Дневник ----------

    fun saveDiary() {
        diaryPrefs.edit { putString("items", Json.encodeToString(selectedItems.toList())) }
    }

    fun clearDay() {
        selectedItems.clear()
        saveDiary()
    }

    // ---------- Продукты ----------

    fun saveProducts() {
        userPrefs.edit { putString("products", Json.encodeToString(productsCache.toList())) }
    }

    fun addProduct(product: Product): Product {
        val withId = product.copy(id = System.currentTimeMillis().toString())
        productsCache.add(withId)
        saveProducts()
        return withId
    }

    fun updateProduct(updated: Product) {
        val idx = productsCache.indexOfFirst { it.id == updated.id }
        if (idx != -1) productsCache[idx] = updated
        saveProducts()
    }

    fun deleteProduct(product: Product) {
        productsCache.removeAll { it.id == product.id }
        saveProducts()
    }

    // ---------- Настройки пользователя ----------

    fun saveUserSettings(weight: Float, age: Int) {
        userWeight = weight
        userAge = age
        userPrefs.edit { putFloat("weight", weight).putInt("age", age) }
    }

    // ---------- Норма калорий ----------

    val dailyNorm: Int get() = (88 + 13 * userWeight + 4.2 * 178 - 5.7 * userAge).toInt()
}