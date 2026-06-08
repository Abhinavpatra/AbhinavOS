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

data class ExerciseTarget(
    val sets: Int,
    val targetWeight: Double?,
    val targetReps: Int?,
    val restSeconds: Int
)

data class WorkoutDayUiState(
    val workout: WorkoutLog? = null,
    val exercises: List<ExerciseLog> = emptyList(),
    val exerciseTargets: Map<String, ExerciseTarget> = emptyMap(),
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
                Pair(workout, sorted)
            }.collect { (workout, sorted) ->
                val allTargets = mutableMapOf<String, ExerciseTarget>()
                for (exercise in sorted) {
                    val sets = dataRepository.getSetsForExercise(exercise.id).first()
                    if (sets.isNotEmpty()) {
                        val first = sets.first()
                        allTargets[exercise.id] = ExerciseTarget(
                            sets = sets.size,
                            targetWeight = first.targetWeight,
                            targetReps = first.targetReps,
                            restSeconds = first.targetRestSeconds
                        )
                    }
                }
                _uiState.value = WorkoutDayUiState(
                    workout = workout,
                    exercises = sorted,
                    exerciseTargets = allTargets,
                    weekWorkouts = _uiState.value.weekWorkouts,
                    isLoading = false
                )
                workout?.let { loadWeekWorkouts(it.weekNumber) }
            }
        }
    }

    fun toggleExerciseCompleted(exerciseId: String) {
        viewModelScope.launch {
            val exercise = _uiState.value.exercises.find { it.id == exerciseId } ?: return@launch
            val sets = dataRepository.getSetsForExercise(exerciseId).first()
            val newCompleted = !exercise.isCompleted
            dataRepository.updateSets(sets.map { it.copy(isCompleted = newCompleted) })
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
