package com.example.athleteos

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.athleteos.features.armor.DailyArmorScreen
import com.example.athleteos.features.history.HistoryScreen
import com.example.athleteos.features.home.HomeScreen
import com.example.athleteos.features.metrics.MetricsScreen
import com.example.athleteos.features.settings.SettingsScreen
import com.example.athleteos.features.weeks.WeekDetailScreen
import com.example.athleteos.features.weeks.WeeksScreen
import com.example.athleteos.features.workout.WorkoutDayScreen
import com.example.athleteos.features.workout.WorkoutExecutionScreen

@Composable
fun MainNavigation() {
  val backStack = rememberNavBackStack(NavigationKeys.Home)

  NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryProvider =
      entryProvider {
        entry<NavigationKeys.Home> {
          HomeScreen(
            onItemClick = { key -> backStack.add(key) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<NavigationKeys.WeeksList> {
          WeeksScreen(
            onItemClick = { key -> backStack.add(key) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<NavigationKeys.WeekDetail> {
          WeekDetailScreen(
            weekNumber = NavigationState.selectedWeekNumber,
            onItemClick = { key -> backStack.add(key) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<NavigationKeys.WorkoutDay> {
          WorkoutDayScreen(
            workoutId = NavigationState.selectedWorkoutId,
            onItemClick = { key -> backStack.add(key) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<NavigationKeys.WorkoutExecution> {
          WorkoutExecutionScreen(
            workoutId = NavigationState.selectedWorkoutId,
            onItemClick = { key -> backStack.add(key) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<NavigationKeys.DailyArmor> {
          DailyArmorScreen(
            onBack = { backStack.removeLastOrNull() },
            onItemClick = { key -> backStack.add(key) }
          )
        }
        entry<NavigationKeys.Metrics> {
          MetricsScreen(
            onBack = { backStack.removeLastOrNull() },
            onItemClick = { key -> backStack.add(key) }
          )
        }
        entry<NavigationKeys.History> {
          HistoryScreen(
            onItemClick = { key -> backStack.add(key) },
            onBack = { backStack.removeLastOrNull() }
          )
        }
        entry<NavigationKeys.Settings> {
          SettingsScreen(
            onBack = { backStack.removeLastOrNull() },
            onItemClick = { key -> backStack.add(key) }
          )
        }
      },
  )
}
