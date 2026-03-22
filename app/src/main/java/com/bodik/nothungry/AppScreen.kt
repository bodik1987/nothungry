package com.bodik.nothungry

import androidx.compose.runtime.Composable
import com.bodik.nothungry.data.AppScreen
import com.bodik.nothungry.data.HungerViewModel
import com.bodik.nothungry.ui.screens.BonAppetit
import com.bodik.nothungry.ui.screens.Distract
import com.bodik.nothungry.ui.screens.HungerCheck
import com.bodik.nothungry.ui.screens.HungerConfirm
import com.bodik.nothungry.ui.screens.LateNight
import com.bodik.nothungry.ui.screens.LightMeal
import com.bodik.nothungry.ui.screens.Unsure

@Composable
fun AppContent(
    viewModel: HungerViewModel,
) {
    val screen = viewModel.currentScreen

    when (screen) {
        AppScreen.LATE_NIGHT -> LateNight(
            onHungry = { viewModel.navigateTo(AppScreen.HUNGER_CONFIRM) },
            onLightMeal = { viewModel.navigateTo(AppScreen.LIGHT_MEAL) },
        )

        AppScreen.HUNGER_CHECK -> HungerCheck(
            onBored = { viewModel.navigateTo(AppScreen.DISTRACT) },
            onHungry = { viewModel.navigateTo(AppScreen.HUNGER_CONFIRM) },
            onUnsure = { viewModel.navigateTo(AppScreen.UNSURE) },
        )

        AppScreen.DISTRACT -> Distract()

        AppScreen.HUNGER_CONFIRM -> HungerConfirm(
            onConfirm = { viewModel.navigateTo(AppScreen.BON_APPETIT) },
            onUnsure = { viewModel.navigateTo(AppScreen.UNSURE) },
            onLightMeal = { viewModel.navigateTo(AppScreen.LIGHT_MEAL) }
        )

        AppScreen.BON_APPETIT -> BonAppetit(
            onLightMeal = { viewModel.navigateTo(AppScreen.LIGHT_MEAL) }
        )

        AppScreen.LIGHT_MEAL -> LightMeal()

        AppScreen.UNSURE -> Unsure(
            onBored = { viewModel.navigateTo(AppScreen.DISTRACT) }
        )
    }
}