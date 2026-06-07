package com.example.athleteos.features.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.DataRepository
import com.example.athleteos.database.ExerciseLog
import com.example.athleteos.database.WorkoutLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutDayUiState(
    val workout: WorkoutLog? = null,
    val exercises: List<ExerciseLog> = emptyList(),
    val weekWorkouts: List<WorkoutLog> = emptyList(),
    val progress: Int = 0,
    val isLoading: Boolean = true
)

@HiltViewModel
class WorkoutDayViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutDayUiState())
    val uiState: StateFlow<WorkoutDayUiState> = _uiState.asStateFlow()
    private var loadJob: Job? = null

    fun loadWorkout(workoutId: Long) {
        loadJob?.cancel()
        _uiState.value = WorkoutDayUiState(isLoading = true)

        loadJob = viewModelScope.launch {
            combine(
                dataRepository.getWorkoutByIdFlow(workoutId),
                dataRepository.getExercisesForWorkout(workoutId)
            ) { workout, exercises ->
                val sorted = exercises.sortedBy { it.orderIndex }
                WorkoutDayUiState(
                    workout = workout,
                    exercises = sorted,
                    weekWorkouts = _uiState.value.weekWorkouts,
                    isLoading = false
                )
            }.collect { state ->
                _uiState.value = state
                state.workout?.let { loadWeekWorkouts(it.weekNumber) }
            }
        }
    }

    private fun loadWeekWorkouts(weekNumber: Int) {
        viewModelScope.launch {
            dataRepository.getWorkoutsForWeek(weekNumber).collect { workouts ->
                _uiState.value = _uiState.value.copy(weekWorkouts = workouts)
            }
        }
    }
}
