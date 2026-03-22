package com.bodik.nothungry.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bodik.nothungry.ui.theme.ITEM_SPACING
import com.bodik.nothungry.ui.theme.RADIUS_INNER
import com.bodik.nothungry.ui.theme.RADIUS_OUTER

public fun columnShape(index: Int, lastIndex: Int) = when {
    lastIndex == 0 -> RoundedCornerShape(RADIUS_OUTER)
    index == 0 -> RoundedCornerShape(
        topStart = RADIUS_OUTER, topEnd = RADIUS_OUTER,
        bottomStart = RADIUS_INNER, bottomEnd = RADIUS_INNER
    )

    index == lastIndex -> RoundedCornerShape(
        topStart = RADIUS_INNER, topEnd = RADIUS_INNER,
        bottomStart = RADIUS_OUTER, bottomEnd = RADIUS_OUTER
    )

    else -> RoundedCornerShape(RADIUS_INNER)
}

data class IslandListItem(
    val id: String,
    val label: String,
    val supportingText: String? = null,
    val leadingContent: (@Composable () -> Unit)? = null,
    val trailingContent: (@Composable () -> Unit)? = null,
    val onClick: (() -> Unit)? = null,
)

@Composable
fun IslandColumn(
    items: List<IslandListItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(ITEM_SPACING)
    ) {
        items.forEachIndexed { index, item ->
            ListItem(
                headlineContent = {
                    Text(
                        item.label, modifier = Modifier.padding(vertical = 4.dp), maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingContent = item.supportingText?.let {
                    {
                        Text(
                            it, maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                leadingContent = item.leadingContent,
                trailingContent = item.trailingContent,
                colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier
                    .clip(columnShape(index, items.lastIndex))
                    .then(
                        if (item.onClick != null) Modifier.clickable { item.onClick.invoke() }
                        else Modifier
                    )
            )
        }
    }
}