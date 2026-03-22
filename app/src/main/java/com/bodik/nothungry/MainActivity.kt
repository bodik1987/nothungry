package com.bodik.nothungry

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.bodik.nothungry.data.HungerViewModel
import com.bodik.nothungry.ui.theme.NothungryTheme

class MainActivity : ComponentActivity() {
    private val viewModel: HungerViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NothungryTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BackHandler {
                        val handled = viewModel.navigateBack()
                        if (!handled) finish()
                    }
                    AppContent(viewModel = viewModel)
                }
            }
        }
    }
}