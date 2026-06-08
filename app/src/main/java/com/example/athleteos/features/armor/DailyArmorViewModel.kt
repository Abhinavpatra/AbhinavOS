package com.example.athleteos.features.armor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.athleteos.data.DataRepository
import com.example.athleteos.data.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

data class DailyArmorUiState(
    val armorItems: List<ArmorItem> = emptyList(),
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val isTodayCompleted: Boolean = false,
    val isLoading: Boolean = true
)

data class ArmorItem(
    val name: String,
    val isCompleted: Boolean
)

@HiltViewModel
class DailyArmorViewModel @Inject constructor(
    private val dataRepository: DataRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val armorItems = listOf(
        "Tibialis Raise", "Single Leg Calf Raise", "Spanish Squat Hold",
        "Pallof Press", "Side Plank", "Cable External Rotation",
        "Pitcher's Path", "Neck Flexion", "Neck Extension", "Neck Side Flexion"
    )

    private val _uiState = MutableStateFlow(DailyArmorUiState())
    val uiState: StateFlow<DailyArmorUiState> = _uiState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }

    init {
        loadData()
    }

    private fun loadData() {
        val today = getTodayDate()
        viewModelScope.launch {
            preferencesRepository.currentStreakFlow.collect { streak ->
                _uiState.value = _uiState.value.copy(currentStreak = streak)
            }
        }
        viewModelScope.launch {
            preferencesRepository.longestStreakFlow.collect { streak ->
                _uiState.value = _uiState.value.copy(longestStreak = streak)
            }
        }
        viewModelScope.launch {
            val armorLog = dataRepository.getDailyArmorLogDirect(today)
            val completedItems = if (armorLog != null) {
                try {
                    json.decodeFromString<List<String>>(armorLog.completedItemsJson)
                } catch (e: Exception) {
                    emptyList()
                }
            } else {
                emptyList()
            }
            val items = armorItems.map { ArmorItem(it, it in completedItems) }
            _uiState.value = _uiState.value.copy(
                armorItems = items,
                isTodayCompleted = completedItems.size == armorItems.size,
                isLoading = false
            )
        }
    }

    fun toggleItem(itemName: String) {
        val today = getTodayDate()
        viewModelScope.launch {
            val currentItems = _uiState.value.armorItems.toMutableList()
            val index = currentItems.indexOfFirst { it.name == itemName }
            if (index == -1) return@launch
            val newItem = currentItems[index].copy(isCompleted = !currentItems[index].isCompleted)
            currentItems[index] = newItem

            val completedNames = currentItems.filter { it.isCompleted }.map { it.name }
            dataRepository.saveDailyArmorLog(today, completedNames)

            _uiState.value = _uiState.value.copy(
                armorItems = currentItems,
                isTodayCompleted = completedNames.size == armorItems.size
            )

            preferencesRepository.updateArmorStreak(today)
        }
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
