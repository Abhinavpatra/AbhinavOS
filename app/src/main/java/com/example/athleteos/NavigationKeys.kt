package com.example.athleteos

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

object NavigationKeys {
    @Serializable data object Home : NavKey
    @Serializable data object WeeksList : NavKey
    @Serializable data object WeekDetail : NavKey
    @Serializable data object WorkoutDay : NavKey
    @Serializable data object WorkoutExecution : NavKey
    @Serializable data object DailyArmor : NavKey
    @Serializable data object Metrics : NavKey
    @Serializable data object History : NavKey
    @Serializable data object Settings : NavKey
}
