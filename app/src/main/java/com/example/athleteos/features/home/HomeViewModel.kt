package com.example.athleteos.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.DataRepository
import com.example.athleteos.database.WorkoutLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class WeekSummary(
    val weekNumber: Int,
    val completionPercentage: Int,
    val totalDuration: Int,
    val isCurrentWeek: Boolean
)

data class HomeUiState(
    val todayWorkout: WorkoutLog? = null,
    val tomorrowWorkout: WorkoutLog? = null,
    val currentWeekNumber: Int = 1,
    val weekWorkouts: List<WorkoutLog> = emptyList(),
    val weeks: List<WeekSummary> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            dataRepository.checkAndSeedDatabase()

            val currentWeek = getCurrentWeekNumber()
            val todayDayNumber = getTodayDayNumber()
            val tomorrowDayNumber = if (todayDayNumber < 7) todayDayNumber + 1 else 1
            val tomorrowWeek = if (todayDayNumber < 7) currentWeek else currentWeek + 1

            dataRepository.getAllWorkouts().collect { workouts ->
                if (workouts.isEmpty()) return@collect
                val today = workouts.find { it.weekNumber == currentWeek && it.dayNumber == todayDayNumber }
                val tomorrow = workouts.find { it.weekNumber == tomorrowWeek && it.dayNumber == tomorrowDayNumber }
                val weekWorkouts = workouts.filter { it.weekNumber == currentWeek }.sortedBy { it.dayNumber }

                val weeks = (1..7).map { weekNum ->
                    val weekWorkoutList = workouts.filter { it.weekNumber == weekNum }
                    val totalDuration = weekWorkoutList.sumOf { it.estimatedDurationMinutes }
                    val avgPct = if (weekWorkoutList.isNotEmpty()) weekWorkoutList.sumOf { it.completionPercentage } / weekWorkoutList.size else 0
                    WeekSummary(
                        weekNumber = weekNum,
                        completionPercentage = avgPct,
                        totalDuration = totalDuration,
                        isCurrentWeek = weekNum == currentWeek
                    )
                }

                _uiState.value = HomeUiState(
                    todayWorkout = today,
                    tomorrowWorkout = tomorrow,
                    currentWeekNumber = currentWeek,
                    weekWorkouts = weekWorkouts,
                    weeks = weeks,
                    isLoading = false
                )
            }
        }
    }

    fun getCurrentWeekNumber(): Int {
        val programStartDate = getProgramStartDate()
        val now = Calendar.getInstance()
        val diffMs = now.timeInMillis - programStartDate.timeInMillis
        val daysDiff = (diffMs / (1000 * 60 * 60 * 24)).toInt()
        val weekNumber = (daysDiff / 7) + 1
        return weekNumber.coerceIn(1, 7)
    }

    fun getTodayDayNumber(): Int {
        val cal = Calendar.getInstance()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        return when (dayOfWeek) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    }

    private fun getProgramStartDate(): Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }
}
