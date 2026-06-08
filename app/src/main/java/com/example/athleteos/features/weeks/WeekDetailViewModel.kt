package com.example.athleteos.features.weeks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.DataRepository
import com.example.athleteos.database.WorkoutLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WeekDetailUiState(
    val weekNumber: Int = 1,
    val workouts: List<WorkoutLog> = emptyList(),
    val overallProgress: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class WeekDetailViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeekDetailUiState())
    val uiState: StateFlow<WeekDetailUiState> = _uiState.asStateFlow()

    fun loadWeek(weekNumber: Int) {
        _uiState.value = _uiState.value.copy(weekNumber = weekNumber)
        viewModelScope.launch {
            dataRepository.getWorkoutsForWeek(weekNumber).collect { workouts ->
                val sorted = workouts.sortedBy { it.dayNumber }
                val progress = if (sorted.isNotEmpty()) sorted.sumOf { it.completionPercentage } / sorted.size else 0
                _uiState.value = WeekDetailUiState(
                    weekNumber = weekNumber,
                    workouts = sorted,
                    overallProgress = progress,
                    isLoading = false
                )
            }
        }
    }

}
