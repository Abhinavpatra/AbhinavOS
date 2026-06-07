package com.example.athleteos.features.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.DataRepository
import com.example.athleteos.database.WorkoutLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HistoryUiState(
    val currentMonth: Int = Calendar.getInstance().get(Calendar.MONTH),
    val currentYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val dayStatuses: Map<String, WorkoutStatus> = emptyMap(),
    val selectedDayWorkouts: List<WorkoutLog> = emptyList(),
    val isLoading: Boolean = true
)

enum class WorkoutStatus { COMPLETED, PARTIAL, MISSED, EMPTY }

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    private fun loadHistory() {
        viewModelScope.launch {
            dataRepository.getAllWorkouts().collect { workouts ->
                updateMonthView(
                    _uiState.value.currentMonth,
                    _uiState.value.currentYear,
                    workouts
                )
            }
        }
    }

    fun previousMonth() {
        val state = _uiState.value
        val cal = Calendar.getInstance().apply {
            set(state.currentYear, state.currentMonth, 1)
            add(Calendar.MONTH, -1)
        }
        changeMonth(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
    }

    fun nextMonth() {
        val state = _uiState.value
        val cal = Calendar.getInstance().apply {
            set(state.currentYear, state.currentMonth, 1)
            add(Calendar.MONTH, 1)
        }
        changeMonth(cal.get(Calendar.MONTH), cal.get(Calendar.YEAR))
    }

    private fun changeMonth(month: Int, year: Int) {
        _uiState.value = _uiState.value.copy(currentMonth = month, currentYear = year)
        viewModelScope.launch {
            val workouts = dataRepository.getAllWorkouts().first()
            updateMonthView(month, year, workouts)
        }
    }

    fun selectDay(day: Int) {
        val state = _uiState.value
        viewModelScope.launch {
            val workouts = dataRepository.getAllWorkouts().first()
            val dateKey = String.format("%04d-%02d-%02d", state.currentYear, state.currentMonth + 1, day)
            val dayWorkouts = workouts.filter { workout ->
                workout.dateCompleted?.let { completed -> 
                    val cal = Calendar.getInstance().apply { timeInMillis = completed }
                    String.format("%04d-%02d-%02d", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH)) == dateKey
                } ?: false
            }
            _uiState.value = _uiState.value.copy(selectedDayWorkouts = dayWorkouts)
        }
    }

    private fun updateMonthView(month: Int, year: Int, workouts: List<WorkoutLog>) {
        val dayStatuses = mutableMapOf<String, WorkoutStatus>()

        val cal = Calendar.getInstance().apply { set(year, month, 1) }
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..daysInMonth) {
            val dateKey = String.format("%04d-%02d-%02d", year, month + 1, day)
            val dayWorkouts = workouts.filter { workout ->
                workout.dateCompleted?.let { completed ->
                    val completedCal = Calendar.getInstance().apply { timeInMillis = completed }
                    String.format("%04d-%02d-%02d", completedCal.get(Calendar.YEAR), completedCal.get(Calendar.MONTH) + 1, completedCal.get(Calendar.DAY_OF_MONTH)) == dateKey
                } ?: false
            }
            dayStatuses[dateKey] = when {
                dayWorkouts.isEmpty() -> WorkoutStatus.EMPTY
                dayWorkouts.all { it.isCompleted } -> WorkoutStatus.COMPLETED
                dayWorkouts.any { it.isCompleted } -> WorkoutStatus.PARTIAL
                else -> WorkoutStatus.MISSED
            }
        }

        _uiState.value = _uiState.value.copy(dayStatuses = dayStatuses, isLoading = false)
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(selectedDayWorkouts = emptyList())
    }
}
