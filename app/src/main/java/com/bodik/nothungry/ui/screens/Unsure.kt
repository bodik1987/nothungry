package com.bodik.nothungry.ui.screens

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bodik.nothungry.ui.components.BottomInfo
import com.bodik.nothungry.ui.components.IslandColumn
import com.bodik.nothungry.ui.components.IslandListItem
import com.bodik.nothungry.ui.components.ScreenLayout
import com.bodik.nothungry.ui.theme.DEFAULT_SPACER
import com.bodik.nothungry.ui.theme.ITEM_SPACING
import com.bodik.nothungry.ui.theme.RADIUS_OUTER

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

        Spacer(modifier = Modifier.height(DEFAULT_SPACER))

        Button(
            onClick = onLightMeal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(ITEM_SPACING),
            contentPadding = PaddingValues(DEFAULT_SPACER),
            shape = RoundedCornerShape(RADIUS_OUTER),
        ) {
            Text("Лёгкий приём пищи", style = MaterialTheme.typography.bodyLarge)
        }

        BottomInfo("Ограничения не навсегда - просто сейчас телу нужна пауза от тяжёлого")
    }
}