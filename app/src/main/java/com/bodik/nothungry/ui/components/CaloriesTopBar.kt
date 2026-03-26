package com.bodik.nothungry.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CaloriesTopBar(
    totalCalories: Int,
    dailyNorm: Int,
    navigationIcon: @Composable () -> Unit,
    bottomContent: @Composable ColumnScope.() -> Unit = {},
) {
    val restCalories = dailyNorm - totalCalories
    val progressFactor = totalCalories.toFloat() / dailyNorm
    val progressColor = when {
        progressFactor >= 1f -> MaterialTheme.colorScheme.error
        progressFactor >= 0.8f -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.primary
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "$totalCalories",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = " / $dailyNorm",
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 4.dp)
                )
                Text(
                    text = "ост. $restCalories",
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }
            navigationIcon()
        }

        Spacer(modifier = Modifier.height(12.dp))

        LinearProgressIndicator(
            progress = { progressFactor.coerceAtMost(1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = progressColor,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )

        bottomContent()
    }
}