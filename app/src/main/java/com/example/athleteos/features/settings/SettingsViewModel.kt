package com.example.athleteos.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val notificationsEnabled: Boolean = true,
    val restTimerDefault: Int = 90,
    val isMetric: Boolean = true,
    val isLoading: Boolean = true
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = _uiState.value.copy(isLoading = false)
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

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setNotificationsEnabled(enabled) }
    }

    fun setRestTimerDefault(seconds: Int) {
        viewModelScope.launch { preferencesRepository.setRestTimerDefault(seconds) }
    }

    fun setIsMetric(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setIsMetric(enabled) }
    }
}
