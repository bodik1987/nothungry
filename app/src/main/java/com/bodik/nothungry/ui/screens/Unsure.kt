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
    onBored: () -> Unit,
) {
    val items = listOf(
        IslandListItem("1", "Будешь ли рад, что съел это, через два часа?"),
        IslandListItem(
            "2",
            "Назови 3 причины зачем это есть: часто уже перечисляя понимаешь, что не нужно"
        ),
        IslandListItem("3", "Всего одна конфета - примерно 10 минут бега в среднем темпе"),
    )

    ScreenLayout(
        title = "Не уверен?",
        subtitle = "Потерпи. 90% импульсов проходят за 15 минут"
    ) {
        IslandColumn(items = items)

        Spacer(modifier = Modifier.height(DEFAULT_SPACER))

        Button(
            onClick = onBored,
            modifier = Modifier
                .fillMaxWidth()
                .padding(ITEM_SPACING),
            contentPadding = PaddingValues(
                horizontal = DEFAULT_SPACER,
                vertical = DEFAULT_SPACER * 2
            ),
            shape = RoundedCornerShape(RADIUS_OUTER),
        ) {
            Text("Наверное, мне просто скучно", style = MaterialTheme.typography.bodyLarge)
        }

        BottomInfo("Ограничения не навсегда - просто сейчас телу нужна пауза от тяжёлого")
    }
}