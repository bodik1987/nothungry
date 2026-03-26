package com.bodik.nothungry.data

import kotlinx.serialization.Serializable

@Serializable
data class Meal(
    val id: String,
    val name: String = "Приём пищи",
    val items: List<Product> = emptyList(),
) {
    val totalCalories: Int
        get() = items.sumOf { (it.calories * it.weight) / 100 }
}