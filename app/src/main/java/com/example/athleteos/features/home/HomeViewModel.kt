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
            val tomorrowDayNumber = if (todayDayNumber in 1..6) todayDayNumber + 1 else 1
            val tomorrowWeek = if (todayDayNumber == 7) currentWeek + 1 else currentWeek

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
        return cal.get(Calendar.DAY_OF_WEEK) // SUNDAY=1, MONDAY=2, ..., SATURDAY=7
    }

    private fun getProgramStartDate(): Calendar {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal
    }
}
