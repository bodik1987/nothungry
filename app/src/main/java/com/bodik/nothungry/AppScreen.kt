package com.bodik.nothungry

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.bodik.nothungry.data.AppScreen
import com.bodik.nothungry.data.CaloriesViewModel
import com.bodik.nothungry.data.HungerViewModel
import com.bodik.nothungry.ui.screens.DayCalories
import com.bodik.nothungry.ui.screens.HungerCheck
import com.bodik.nothungry.ui.screens.LateNight
import com.bodik.nothungry.ui.screens.LightMeal
import com.bodik.nothungry.ui.screens.SearchScreen
import com.bodik.nothungry.ui.screens.Unsure

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun AppContent(
    viewModel: HungerViewModel,
    caloriesViewModel: CaloriesViewModel,
) {
    val screen = viewModel.currentScreen

    when (screen) {
        AppScreen.LATE_NIGHT -> LateNight(
            onHungry = { viewModel.navigateTo(AppScreen.CALORIES) },
            onLightMeal = { viewModel.navigateTo(AppScreen.LIGHT_MEAL) },
        )

        AppScreen.HUNGER_CHECK -> HungerCheck(
            onHungry = { viewModel.navigateTo(AppScreen.CALORIES) },
            onUnsure = { viewModel.navigateTo(AppScreen.UNSURE) },
        )

        AppScreen.LIGHT_MEAL -> LightMeal()

        AppScreen.UNSURE -> Unsure(
            onLightMeal = { viewModel.navigateTo(AppScreen.LIGHT_MEAL) }
        )

        AppScreen.CALORIES -> DayCalories(
            viewModel = caloriesViewModel,
            onNavigateToSearch = { mealId -> viewModel.navigateToSearch(mealId) },
            onLightMeal = { viewModel.navigateTo(AppScreen.LIGHT_MEAL) }
        )

        AppScreen.CALORIES_SEARCH -> SearchScreen(
            viewModel = caloriesViewModel,
            mealId = viewModel.currentMealId ?: "",
            onBack = { viewModel.navigateBack() }
        )
    }
}