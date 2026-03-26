package com.bodik.nothungry.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.Calendar

enum class AppScreen {
    LATE_NIGHT,
    HUNGER_CHECK,
    LIGHT_MEAL,
    UNSURE,
    CALORIES,
    CALORIES_SEARCH,
}

class HungerViewModel : ViewModel() {

    private val backStack = mutableListOf<AppScreen>()

    var currentScreen by mutableStateOf(getInitialScreen())
        private set

    var currentMealId by mutableStateOf<String?>(null)
        private set

    fun navigateTo(screen: AppScreen) {
        backStack.add(currentScreen)
        currentScreen = screen
    }

    fun navigateToSearch(mealId: String) {
        currentMealId = mealId
        navigateTo(AppScreen.CALORIES_SEARCH)
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun navigateBack(): Boolean {
        return if (backStack.isNotEmpty()) {
            currentScreen = backStack.removeLast()
            true
        } else {
            false
        }
    }

    private fun getInitialScreen(): AppScreen {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return if (hour >= 18) AppScreen.LATE_NIGHT else AppScreen.HUNGER_CHECK
    }
}