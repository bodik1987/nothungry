package com.bodik.nothungry.ui.screens

import androidx.compose.runtime.Composable
import com.bodik.nothungry.ui.components.ButtonGroup
import com.bodik.nothungry.ui.components.ButtonGroupItem
import com.bodik.nothungry.ui.components.ScreenLayout

@Composable
fun HungerCheck(
    onBored: () -> Unit,
    onHungry: () -> Unit,
    onUnsure: () -> Unit,
) {
    ScreenLayout(
        title = "Хочешь есть?",
        subtitle = "Это точно голод?",
    ) {
        ButtonGroup(
            items = listOf(
                ButtonGroupItem("Скучно / стресс", onBored),
                ButtonGroupItem("Не уверен", onUnsure),
                ButtonGroupItem("Я голоден", onHungry),
            )
        )
    }
}