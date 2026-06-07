package com.example.athleteos.features.weeks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.DataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeekSummary(
    val weekNumber: Int,
    val completionPercentage: Int,
    val totalDuration: Int,
    val isCurrentWeek: Boolean
)

data class WeeksUiState(
    val weeks: List<WeekSummary> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class WeeksViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeeksUiState())
    val uiState: StateFlow<WeeksUiState> = _uiState.asStateFlow()

    init {
        loadWeeks()
    }

    private fun loadWeeks() {
        viewModelScope.launch {
            dataRepository.getAllWorkouts().collect { workouts ->
                val currentWeek = getCurrentWeekNumber()
                val weeks = (1..7).map { weekNum ->
                    val weekWorkouts = workouts.filter { it.weekNumber == weekNum }
                    val totalDuration = weekWorkouts.sumOf { it.estimatedDurationMinutes }
                    val avgPct = if (weekWorkouts.isNotEmpty()) weekWorkouts.sumOf { it.completionPercentage } / weekWorkouts.size else 0
                    WeekSummary(
                        weekNumber = weekNum,
                        completionPercentage = avgPct,
                        totalDuration = totalDuration,
                        isCurrentWeek = weekNum == currentWeek
                    )
                }
                _uiState.value = WeeksUiState(weeks = weeks, isLoading = false)
            }
        }
    }

    private fun getCurrentWeekNumber(): Int {
        val cal = java.util.Calendar.getInstance()
        val startCal = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val diffMs = cal.timeInMillis - startCal.timeInMillis
        val daysDiff = (diffMs / (1000 * 60 * 60 * 24)).toInt()
        return (daysDiff / 7 + 1).coerceIn(1, 7)
    }
}
