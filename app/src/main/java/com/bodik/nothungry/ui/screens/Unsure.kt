package com.bodik.nothungry.ui.screens

import androidx.compose.runtime.Composable
import com.bodik.nothungry.ui.components.BottomInfo
import com.bodik.nothungry.ui.components.IslandColumn
import com.bodik.nothungry.ui.components.IslandListItem
import com.bodik.nothungry.ui.components.ScreenLayout

@Composable
fun Unsure(
    onLightMeal: () -> Unit
) {
    val items = listOf(
        IslandListItem("1", "Потом же будет тяжело!"),
        IslandListItem(
            "2",
            "Зачем это есть?"
        ),
        IslandListItem("3", "1 конфета - 10 минут бега"),
        IslandListItem("4", "Выйди прогуляйся"),
        IslandListItem("5", "Поработай за компьютером"),
        IslandListItem("6", "Выпей воды, чаю, кофе"),
        IslandListItem("7", "Сделай уборку"),
        IslandListItem("8", "Поставь таймер на 15 минут"),
        IslandListItem("9", "Сделай 10 глубоких вдохов"),
    )

    ScreenLayout(
        title = "Не уверен?",
    ) {
        IslandColumn(items = items)

        BottomInfo("Ограничения не навсегда - просто сейчас телу нужна пауза от тяжёлого")
    }
}