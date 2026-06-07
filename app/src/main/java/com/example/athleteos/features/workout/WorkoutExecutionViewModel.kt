package com.example.athleteos.features.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.DataRepository
import com.example.athleteos.database.ExerciseLog
import com.example.athleteos.database.ExerciseSetLog
import com.example.athleteos.database.WorkoutLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WorkoutExecutionUiState(
    val workout: WorkoutLog? = null,
    val exercises: List<ExerciseLog> = emptyList(),
    val exerciseSets: Map<String, List<ExerciseSetLog>> = emptyMap(),
    val expandedExerciseId: String? = null,
    val restTimerSeconds: Int = 0,
    val isRestTimerRunning: Boolean = false,
    val isLoading: Boolean = true
)

@HiltViewModel
class WorkoutExecutionViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WorkoutExecutionUiState())
    val uiState: StateFlow<WorkoutExecutionUiState> = _uiState.asStateFlow()

    fun loadWorkout(workoutId: Long) {
        viewModelScope.launch {
            dataRepository.getWorkoutByIdFlow(workoutId).collect { workout ->
                if (workout != null) {
                    _uiState.value = _uiState.value.copy(workout = workout)
                }
            }
        }
        viewModelScope.launch {
            dataRepository.getExercisesForWorkout(workoutId).collect { exercises ->
                val sorted = exercises.sortedBy { it.orderIndex }
                _uiState.value = _uiState.value.copy(exercises = sorted, isLoading = false)
                sorted.forEach { exercise ->
                    viewModelScope.launch {
                        dataRepository.getSetsForExercise(exercise.id).collect { sets ->
                            val updatedSets = _uiState.value.exerciseSets.toMutableMap()
                            updatedSets[exercise.id] = sets.sortedBy { it.setNumber }
                            _uiState.value = _uiState.value.copy(exerciseSets = updatedSets)
                        }
                    }
                }
            }
        }
    }

    fun toggleExerciseExpanded(exerciseId: String) {
        val current = _uiState.value.expandedExerciseId
        _uiState.value = _uiState.value.copy(expandedExerciseId = if (current == exerciseId) null else exerciseId)
    }

    fun completeSet(set: ExerciseSetLog) {
        viewModelScope.launch {
            val updatedSet = set.copy(
                isCompleted = !set.isCompleted,
                actualWeight = set.actualWeight ?: set.targetWeight,
                actualReps = set.actualReps ?: set.targetReps
            )
            dataRepository.updateSet(updatedSet)

            if (updatedSet.isCompleted) {
                val restSec = set.targetRestSeconds
                _uiState.value = _uiState.value.copy(
                    restTimerSeconds = restSec,
                    isRestTimerRunning = true
                )
            }
        }
    }

    fun updateActualWeight(set: ExerciseSetLog, weight: Double) {
        viewModelScope.launch {
            dataRepository.updateSet(set.copy(actualWeight = weight))
        }
    }

    fun updateActualReps(set: ExerciseSetLog, reps: Int) {
        viewModelScope.launch {
            dataRepository.updateSet(set.copy(actualReps = reps))
        }
    }

    fun completeExercise(exerciseId: String) {
        viewModelScope.launch {
            val sets = _uiState.value.exerciseSets[exerciseId] ?: return@launch
            val updatedSets = sets.map { it.copy(isCompleted = true, actualWeight = it.actualWeight ?: it.targetWeight, actualReps = it.actualReps ?: it.targetReps) }
            dataRepository.updateSets(updatedSets)
        }
    }

    fun undoLastCompletion() {
        val allSets = _uiState.value.exerciseSets.values.flatten().sortedByDescending { it.id }
        val lastCompleted = allSets.firstOrNull { it.isCompleted } ?: return
        viewModelScope.launch {
            dataRepository.updateSet(lastCompleted.copy(isCompleted = false))
        }
    }

    fun stopRestTimer() {
        _uiState.value = _uiState.value.copy(isRestTimerRunning = false, restTimerSeconds = 0)
    }

    fun tickRestTimer() {
        val current = _uiState.value.restTimerSeconds
        if (current > 0) {
            _uiState.value = _uiState.value.copy(restTimerSeconds = current - 1)
        } else {
            _uiState.value = _uiState.value.copy(isRestTimerRunning = false)
        }
    }

    fun setRestTimer(seconds: Int) {
        _uiState.value = _uiState.value.copy(restTimerSeconds = seconds, isRestTimerRunning = true)
    }
}
