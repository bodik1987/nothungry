package com.bodik.nothungry.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp

public val RADIUS_OUTER = 16.dp
public val RADIUS_INNER = 4.dp
public val ITEM_SPACING = 2.dp
public val DEFAULT_SPACER = 20.dp

fun getGroupedShape(index: Int, total: Int): RoundedCornerShape {
    val l = 18.dp;
    val s = 2.dp
    return when {
        total == 1 -> RoundedCornerShape(l)
        index == 0 -> RoundedCornerShape(topStart = l, topEnd = l, bottomStart = s, bottomEnd = s)
        index == total - 1 -> RoundedCornerShape(
            topStart = s,
            topEnd = s,
            bottomStart = l,
            bottomEnd = l
        )

        else -> RoundedCornerShape(s)
    }
}