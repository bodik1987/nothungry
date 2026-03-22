package com.bodik.nothungry.ui.screens

import androidx.compose.runtime.Composable
import com.bodik.nothungry.ui.components.ButtonGroup
import com.bodik.nothungry.ui.components.ButtonGroupItem
import com.bodik.nothungry.ui.components.ScreenLayout

@Composable
fun HungerConfirm(
    onConfirm: () -> Unit,
    onUnsure: () -> Unit,
    onLightMeal: () -> Unit
) {
    ScreenLayout(
        title = "Точно голоден?",
        subtitle = "Если сейчас не поешь, через 10 минут будет хуже?"
    ) {
        ButtonGroup(
            items = listOf(
                ButtonGroupItem("Очень голоден", onConfirm),
                ButtonGroupItem("Не уверен", onUnsure),
                ButtonGroupItem("Лёгкий приём пищи", onLightMeal),
            )
        )
    }
}