package com.bodik.nothungry.ui.screens

import androidx.compose.runtime.Composable
import com.bodik.nothungry.ui.components.ButtonGroup
import com.bodik.nothungry.ui.components.ButtonGroupItem
import com.bodik.nothungry.ui.components.ScreenLayout

@Composable
fun HungerCheck(
    onHungry: () -> Unit,
    onUnsure: () -> Unit,
) {
    ScreenLayout(
        title = "Хочешь есть?",
    ) {
        ButtonGroup(
            items = listOf(
                ButtonGroupItem("Не особо", onUnsure),
                ButtonGroupItem("Да", onHungry),
            )
        )
    }
}