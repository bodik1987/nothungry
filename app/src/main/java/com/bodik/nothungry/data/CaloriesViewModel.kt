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

    /** Приёмы пищи за день */
    val meals = mutableStateListOf<Meal>()

    /** Кэш продуктов для поиска (начинается с дефолтного списка) */
    val productsCache = mutableStateListOf<Product>()

    /** Настройки пользователя */
    var userWeight by mutableFloatStateOf(userPrefs.getFloat("weight", 82f))
        private set
    var userAge by mutableIntStateOf(userPrefs.getInt("age", 39))
        private set

    init {
        // Загружаем дневник (приёмы пищи)
        diaryPrefs.getString("meals", null)?.let { saved ->
            try {
                meals.addAll(Json.decodeFromString<List<Meal>>(saved))
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
        diaryPrefs.edit { putString("meals", Json.encodeToString(meals.toList())) }
    }

    fun clearDay() {
        meals.clear()
        saveDiary()
    }

    // ---------- Приёмы пищи ----------

    fun addMeal(name: String): Meal {
        val meal = Meal(id = System.currentTimeMillis().toString(), name = name)
        meals.add(meal)
        saveDiary()
        return meal
    }

    fun renameMeal(mealId: String, newName: String) {
        val idx = meals.indexOfFirst { it.id == mealId }
        if (idx != -1) meals[idx] = meals[idx].copy(name = newName)
        saveDiary()
    }

    fun deleteMeal(mealId: String) {
        meals.removeAll { it.id == mealId }
        saveDiary()
    }

    // ---------- Продукты внутри приёма ----------

    /** Добавить или увеличить вес продукта в конкретном приёме */
    fun addProductToMeal(mealId: String, product: Product) {
        val mealIdx = meals.indexOfFirst { it.id == mealId }
        if (mealIdx == -1) return
        val meal = meals[mealIdx]
        val existingIdx = meal.items.indexOfFirst { it.title == product.title }
        val updatedItems = meal.items.toMutableList()
        if (existingIdx != -1) {
            updatedItems[existingIdx] =
                updatedItems[existingIdx].copy(weight = updatedItems[existingIdx].weight + product.weight)
        } else {
            updatedItems.add(product)
        }
        meals[mealIdx] = meal.copy(items = updatedItems)
        saveDiary()
    }

    fun updateProductInMeal(mealId: String, updated: Product) {
        val mealIdx = meals.indexOfFirst { it.id == mealId }
        if (mealIdx == -1) return
        val meal = meals[mealIdx]
        val updatedItems = meal.items.toMutableList()
        val itemIdx = updatedItems.indexOfFirst { it.id == updated.id }
        if (itemIdx != -1) updatedItems[itemIdx] = updated
        meals[mealIdx] = meal.copy(items = updatedItems)
        saveDiary()
    }

    fun removeProductFromMeal(mealId: String, product: Product) {
        val mealIdx = meals.indexOfFirst { it.id == mealId }
        if (mealIdx == -1) return
        val meal = meals[mealIdx]
        meals[mealIdx] = meal.copy(items = meal.items.filter { it.id != product.id })
        saveDiary()
    }

    // ---------- Продукты (кэш) ----------

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