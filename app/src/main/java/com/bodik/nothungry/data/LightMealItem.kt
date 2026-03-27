package com.bodik.nothungry.data

import kotlinx.serialization.Serializable

@Serializable
data class LightMealItem(
    val id: Long = 0L,
    val name: String,
    val description: String = ""
)