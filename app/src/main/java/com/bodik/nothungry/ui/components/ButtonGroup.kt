package com.bodik.nothungry.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bodik.nothungry.ui.theme.DEFAULT_SPACER
import com.bodik.nothungry.ui.theme.ITEM_SPACING
import com.bodik.nothungry.ui.theme.RADIUS_INNER
import com.bodik.nothungry.ui.theme.RADIUS_OUTER

data class ButtonGroupItem(
    val text: String,
    val onClick: () -> Unit,
)

@Composable
fun ButtonGroup(
    items: List<ButtonGroupItem>,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ITEM_SPACING)
    ) {
        items.chunked(2).forEachIndexed { rowIndex, row ->
            val isFirstRow = rowIndex == 0
            val isLastRow = rowIndex == items.chunked(2).lastIndex

            if (row.size == 1) {
                val item = row[0]
                Button(
                    onClick = item.onClick,
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(
                        topStart = if (isFirstRow) RADIUS_OUTER else RADIUS_INNER,
                        topEnd = if (isFirstRow) RADIUS_OUTER else RADIUS_INNER,
                        bottomStart = if (isLastRow) RADIUS_OUTER else RADIUS_INNER,
                        bottomEnd = if (isLastRow) RADIUS_OUTER else RADIUS_INNER,
                    ),
                    contentPadding = PaddingValues(
                        horizontal = DEFAULT_SPACER,
                        vertical = DEFAULT_SPACER * 2
                    )
                ) {
                    Text(
                        item.text, style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(ITEM_SPACING)
                ) {

                    row.forEachIndexed { colIndex, item ->
                        val isLeft = colIndex == 0
                        Button(
                            onClick = item.onClick,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .heightIn(min = 72.dp),
                            shape = RoundedCornerShape(
                                topStart = if (isFirstRow && isLeft) RADIUS_OUTER else RADIUS_INNER,
                                topEnd = if (isFirstRow && !isLeft) RADIUS_OUTER else RADIUS_INNER,
                                bottomStart = if (isLastRow && isLeft) RADIUS_OUTER else RADIUS_INNER,
                                bottomEnd = if (isLastRow && !isLeft) RADIUS_OUTER else RADIUS_INNER,
                            ),
                            contentPadding = PaddingValues(
                                horizontal = DEFAULT_SPACER,
                                vertical = DEFAULT_SPACER * 2
                            )
                        ) {
                            Text(
                                item.text, style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}