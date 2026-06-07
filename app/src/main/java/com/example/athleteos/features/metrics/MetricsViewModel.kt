package com.example.athleteos.features.metrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.DataRepository
import com.example.athleteos.database.MetricLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class MetricsUiState(
    val selectedCategory: String = "BODY",
    val selectedMetric: String = "Weight",
    val selectedTimeRange: String = "30d",
    val metricHistory: List<MetricLog> = emptyList(),
    val recentMetrics: List<MetricLog> = emptyList(),
    val categoryMetrics: Map<String, List<String>> = emptyMap(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MetricsViewModel @Inject constructor(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetricsUiState())
    val uiState: StateFlow<MetricsUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            dataRepository.getAllMetricLogs().collect { logs ->
                val categories = mapOf(
                    "BODY" to listOf("Weight", "Body Fat", "Waist"),
                    "RECOVERY" to listOf("Sleep", "Resting Heart Rate", "Water"),
                    "STRENGTH" to listOf("Pullups", "Weighted Pullups", "Squat", "Push Press"),
                    "ATHLETICISM" to listOf("Vertical Jump", "Broad Jump", "20m Sprint")
                )
                _uiState.value = _uiState.value.copy(
                    categoryMetrics = categories,
                    recentMetrics = logs,
                    isLoading = false
                )
                loadMetricHistory(_uiState.value.selectedCategory, _uiState.value.selectedMetric)
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        val metrics = _uiState.value.categoryMetrics[category] ?: return
        selectMetric(metrics.first())
    }

    fun selectMetric(metric: String) {
        _uiState.value = _uiState.value.copy(selectedMetric = metric)
        loadMetricHistory(_uiState.value.selectedCategory, metric)
    }

    fun selectTimeRange(range: String) {
        _uiState.value = _uiState.value.copy(selectedTimeRange = range)
        loadMetricHistory(_uiState.value.selectedCategory, _uiState.value.selectedMetric)
    }

    private fun loadMetricHistory(category: String, metric: String) {
        viewModelScope.launch {
            dataRepository.getMetricHistoryFlow(metric).collect { history ->
                val filtered = filterByTimeRange(history)
                _uiState.value = _uiState.value.copy(metricHistory = filtered)
            }
        }
    }

    fun saveMetricValue(value: Double) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val state = _uiState.value
        val category = state.selectedCategory
        val metricName = state.selectedMetric

        val unit = when (metricName) {
            "Weight" -> "kg"; "Body Fat" -> "%"; "Waist" -> "cm"
            "Sleep" -> "hrs"; "Resting Heart Rate" -> "bpm"; "Water" -> "L"
            "Pullups", "Weighted Pullups" -> "reps"; "Squat", "Push Press" -> "kg"
            "Vertical Jump", "Broad Jump" -> "cm"; "20m Sprint" -> "s"
            else -> ""
        }

        viewModelScope.launch {
            dataRepository.saveMetric(today, category, metricName, value, unit)
        }
    }

    private fun filterByTimeRange(logs: List<MetricLog>): List<MetricLog> {
        val now = java.util.Calendar.getInstance()
        val cutoff = java.util.Calendar.getInstance()
        when (_uiState.value.selectedTimeRange) {
            "7d" -> cutoff.add(java.util.Calendar.DAY_OF_YEAR, -7)
            "30d" -> cutoff.add(java.util.Calendar.DAY_OF_YEAR, -30)
            "90d" -> cutoff.add(java.util.Calendar.DAY_OF_YEAR, -90)
            "all" -> return logs
        }
        return logs.filter { log ->
            try {
                val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(log.date) ?: return@filter false
                date.time >= cutoff.timeInMillis
            } catch (e: Exception) {
                false
            }
        }
    }

    fun getPR(): String? {
        val history = _uiState.value.metricHistory
        if (history.isEmpty()) return null
        val name = _uiState.value.selectedMetric
        val higherIsBetter = when (name) {
            "Body Fat", "Resting Heart Rate", "20m Sprint", "Waist" -> false
            else -> true
        }
        val pr = if (higherIsBetter) history.maxByOrNull { it.value } else history.minByOrNull { it.value }
        return pr?.let { "${it.value} ${it.unit} (${it.date})" }
    }
}
