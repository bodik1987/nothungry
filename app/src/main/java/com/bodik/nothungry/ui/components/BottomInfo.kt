package com.bodik.nothungry.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.bodik.nothungry.ui.theme.DEFAULT_SPACER

@Composable
fun BottomInfo(
    text: String,
) {
    Spacer(modifier = Modifier.height(DEFAULT_SPACER * 2))

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    
    Spacer(modifier = Modifier.height(DEFAULT_SPACER * 2))
}