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
import com.bodik.nothungry.ui.components.IslandColumn
import com.bodik.nothungry.ui.components.IslandListItem
import com.bodik.nothungry.ui.components.ScreenLayout
import com.bodik.nothungry.ui.theme.DEFAULT_SPACER
import com.bodik.nothungry.ui.theme.ITEM_SPACING
import com.bodik.nothungry.ui.theme.RADIUS_OUTER

@Composable
fun BonAppetit(onLightMeal: () -> Unit) {

    val items = listOf(
        IslandListItem("1", "Положи меньше - добавить успеешь"),
        IslandListItem("2", "Ешь медленно, жуй дольше"),
        IslandListItem("3", "Вставай чуть голодным"),
        IslandListItem("4", "Уменьшай плотность еды"),
    )

    ScreenLayout(
        title = "Приятного аппетита",
    ) {
        IslandColumn(items = items)

        Spacer(modifier = Modifier.height(DEFAULT_SPACER))

        Button(
            onClick = onLightMeal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = ITEM_SPACING),
            shape = RoundedCornerShape(RADIUS_OUTER),
            contentPadding = PaddingValues(
                horizontal = DEFAULT_SPACER,
                vertical = DEFAULT_SPACER
            )
        ) {
            Text(text = "Лёгкий приём пищи", style = MaterialTheme.typography.bodyLarge)
        }
    }
}