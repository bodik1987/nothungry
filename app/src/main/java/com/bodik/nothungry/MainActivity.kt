package com.bodik.nothungry

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.bodik.nothungry.data.AppScreen
import com.bodik.nothungry.data.CaloriesViewModel
import com.bodik.nothungry.data.HungerViewModel
import com.bodik.nothungry.ui.theme.DEFAULT_SPACER
import com.bodik.nothungry.ui.theme.NothungryTheme
import com.bodik.nothungry.ui.theme.RADIUS_OUTER

class MainActivity : ComponentActivity() {
    private val viewModel: HungerViewModel by viewModels()
    private val caloriesViewModel: CaloriesViewModel by viewModels()

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
                    Box(modifier = Modifier.fillMaxSize()) {
                        AppContent(viewModel = viewModel, caloriesViewModel = caloriesViewModel)

                        // Кнопка поверх всех экранов, скрывается когда уже на экране калорий
                        if (viewModel.currentScreen != AppScreen.CALORIES &&
                            viewModel.currentScreen != AppScreen.CALORIES_SEARCH
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.TopEnd)
                                    .statusBarsPadding()
                                    .padding(end = DEFAULT_SPACER, top = DEFAULT_SPACER),
                                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                            ) {
                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.CALORIES) },
                                    shape = RoundedCornerShape(RADIUS_OUTER),
                                ) {
                                    Text(
                                        text = "Калькулятор калорий",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}