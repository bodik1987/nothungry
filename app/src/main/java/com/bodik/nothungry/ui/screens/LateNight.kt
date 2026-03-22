package com.bodik.nothungry.ui.screens

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bodik.nothungry.ui.components.BottomInfo
import com.bodik.nothungry.ui.components.ButtonGroup
import com.bodik.nothungry.ui.components.ButtonGroupItem
import com.bodik.nothungry.ui.components.IslandColumn
import com.bodik.nothungry.ui.components.IslandListItem
import com.bodik.nothungry.ui.components.ScreenLayout
import com.bodik.nothungry.ui.theme.DEFAULT_SPACER

@Composable
fun LateNight(
    onHungry: () -> Unit,
    onLightMeal: () -> Unit,
) {
    val items = listOf(
        IslandListItem(
            "1",
            "Тяжесть в животе перед сном"
        ),
        IslandListItem("2", "Не выспишься"),
        IslandListItem(
            "3",
            "Завтра будешь рад, что не поел"
        ),
    )

    ScreenLayout(
        title = "Уже поздно!",
        subtitle = "После 18:00 лучше не есть"
    ) {
        IslandColumn(items = items)

        Spacer(modifier = Modifier.height(DEFAULT_SPACER))

        ButtonGroup(
            items = listOf(
                ButtonGroupItem("Очень голоден", onHungry),
                ButtonGroupItem("Лёгкий приём пищи", onLightMeal),
            )
        )

        BottomInfo("Эти ограничения не постоянны, просто сейчас период, когда нужно отказаться от тяжёлой еды")
    }
}