package com.example.athleteos.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.DataRepository
import com.example.athleteos.data.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

data class SettingsUiState(
    val darkMode: Boolean? = null,
    val notificationsEnabled: Boolean = true,
    val restTimerDefault: Int = 90,
    val isMetric: Boolean = true,
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.darkModeFlow.collect { _uiState.value = _uiState.value.copy(darkMode = it, isLoading = false) }
        }
        viewModelScope.launch {
            preferencesRepository.notificationsFlow.collect { _uiState.value = _uiState.value.copy(notificationsEnabled = it) }
        }
        viewModelScope.launch {
            preferencesRepository.restTimerDefaultFlow.collect { _uiState.value = _uiState.value.copy(restTimerDefault = it) }
        }
        viewModelScope.launch {
            preferencesRepository.isMetricFlow.collect { _uiState.value = _uiState.value.copy(isMetric = it) }
        }
    }

    fun setDarkMode(enabled: Boolean?) {
        viewModelScope.launch { preferencesRepository.setDarkMode(enabled) }
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setNotificationsEnabled(enabled) }
    }

    fun setRestTimerDefault(seconds: Int) {
        viewModelScope.launch { preferencesRepository.setRestTimerDefault(seconds) }
    }

    fun setIsMetric(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setIsMetric(enabled) }
    }

    fun exportData() {
        viewModelScope.launch {
            try {
                val json = Json { prettyPrint = true }
                val workouts = dataRepository.getAllWorkouts().first()
                val metrics = dataRepository.getAllMetricLogs().first()
                val armor = dataRepository.getAllDailyArmorLogs().first()
            } catch (e: Exception) {
            }
        }
    }

    fun importData() {
        viewModelScope.launch {
            try {
            } catch (e: Exception) {
            }
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            dataRepository.clearAllData()
        }
    }
}
