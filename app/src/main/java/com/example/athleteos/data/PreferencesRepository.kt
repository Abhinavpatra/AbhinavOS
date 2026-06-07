package com.example.athleteos.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "athlete_settings")

interface PreferencesRepository {
    val darkModeFlow: Flow<Boolean?>
    val notificationsFlow: Flow<Boolean>
    val restTimerDefaultFlow: Flow<Int>
    val isMetricFlow: Flow<Boolean>
    val currentStreakFlow: Flow<Int>
    val longestStreakFlow: Flow<Int>
    val lastArmorDateFlow: Flow<String?>

    suspend fun setDarkMode(enabled: Boolean?)
    suspend fun setNotificationsEnabled(enabled: Boolean)
    suspend fun setRestTimerDefault(seconds: Int)
    suspend fun setIsMetric(enabled: Boolean)
    suspend fun setCurrentStreak(streak: Int)
    suspend fun setLongestStreak(streak: Int)
    suspend fun setLastArmorDate(date: String?)
    suspend fun updateArmorStreak(today: String)
    suspend fun clearPreferences()
}

class DefaultPreferencesRepository(
    private val context: Context
) : PreferencesRepository {

    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val notificationsKey = booleanPreferencesKey("notifications_enabled")
    private val restTimerDefaultKey = intPreferencesKey("rest_timer_default")
    private val isMetricKey = booleanPreferencesKey("is_metric")
    private val currentStreakKey = intPreferencesKey("current_streak")
    private val longestStreakKey = intPreferencesKey("longest_streak")
    private val lastArmorDateKey = stringPreferencesKey("last_armor_date")

    override val darkModeFlow: Flow<Boolean?> = context.dataStore.data.map { preferences ->
        preferences[darkModeKey]
    }

    override val notificationsFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[notificationsKey] ?: true
    }

    override val restTimerDefaultFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[restTimerDefaultKey] ?: 90
    }

    override val isMetricFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[isMetricKey] ?: true
    }

    override val currentStreakFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[currentStreakKey] ?: 0
    }

    override val longestStreakFlow: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[longestStreakKey] ?: 0
    }

    override val lastArmorDateFlow: Flow<String?> = context.dataStore.data.map { preferences ->
        preferences[lastArmorDateKey]
    }

    override suspend fun setDarkMode(enabled: Boolean?) {
        context.dataStore.edit { preferences ->
            if (enabled == null) {
                preferences.remove(darkModeKey)
            } else {
                preferences[darkModeKey] = enabled
            }
        }
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[notificationsKey] = enabled
        }
    }

    override suspend fun setRestTimerDefault(seconds: Int) {
        context.dataStore.edit { preferences ->
            preferences[restTimerDefaultKey] = seconds
        }
    }

    override suspend fun setIsMetric(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[isMetricKey] = enabled
        }
    }

    override suspend fun setCurrentStreak(streak: Int) {
        context.dataStore.edit { preferences ->
            preferences[currentStreakKey] = streak
        }
    }

    override suspend fun setLongestStreak(streak: Int) {
        context.dataStore.edit { preferences ->
            preferences[longestStreakKey] = streak
        }
    }

    override suspend fun setLastArmorDate(date: String?) {
        context.dataStore.edit { preferences ->
            if (date == null) {
                preferences.remove(lastArmorDateKey)
            } else {
                preferences[lastArmorDateKey] = date
            }
        }
    }

    override suspend fun updateArmorStreak(today: String) {
        context.dataStore.edit { preferences ->
            val lastDate = preferences[lastArmorDateKey]
            val currentStreak = preferences[currentStreakKey] ?: 0
            val longestStreak = preferences[longestStreakKey] ?: 0

            val newStreak = if (lastDate == null) {
                1
            } else {
                val yesterday = yesterdayString(today)
                if (lastDate == yesterday) {
                    currentStreak + 1
                } else if (lastDate == today) {
                    currentStreak
                } else {
                    1
                }
            }

            preferences[currentStreakKey] = newStreak
            preferences[longestStreakKey] = maxOf(longestStreak, newStreak)
            preferences[lastArmorDateKey] = today
        }
    }

    private fun yesterdayString(today: String): String {
        val parts = today.split("-")
        if (parts.size != 3) return today
        val year = parts[0].toIntOrNull() ?: return today
        val month = parts[1].toIntOrNull() ?: return today
        val day = parts[2].toIntOrNull() ?: return today
        val cal = java.util.Calendar.getInstance()
        cal.set(year, month - 1, day)
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        return String.format("%04d-%02d-%02d", cal.get(java.util.Calendar.YEAR), cal.get(java.util.Calendar.MONTH) + 1, cal.get(java.util.Calendar.DAY_OF_MONTH))
    }

    override suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
