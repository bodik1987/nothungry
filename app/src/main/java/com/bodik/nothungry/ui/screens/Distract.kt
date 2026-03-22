package com.bodik.nothungry.ui.screens

import androidx.compose.runtime.Composable
import com.bodik.nothungry.ui.components.IslandColumn
import com.bodik.nothungry.ui.components.IslandListItem
import com.bodik.nothungry.ui.components.ScreenLayout

@Composable
fun Distract() {
    val items = listOf(
        IslandListItem("1", "Выйди на прогулку"),
        IslandListItem("2", "Поработай за компьютером"),
        IslandListItem("3", "Выпей воды, чаю, кофе"),
        IslandListItem("4", "Сделай уборку"),
        IslandListItem("5", "Поставь таймер на 10-15 минут"),
        IslandListItem("6", "Сделай 10 глубоких вдохов"),
    )

    ScreenLayout(
        title = "Перетерпи",
        subtitle = "Скука и стресс - не голод"
    ) {
        IslandColumn(items = items)
    }
}