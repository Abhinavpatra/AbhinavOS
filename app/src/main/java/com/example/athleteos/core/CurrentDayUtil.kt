package com.example.athleteos.core

import java.util.Calendar

data class CurrentDayInfo(
    val weekNumber: Int,
    val dayNumber: Int
)

fun getCurrentDayInfo(): CurrentDayInfo {
    val now = Calendar.getInstance()
    val dayNumber = when (now.get(Calendar.DAY_OF_WEEK)) {
        Calendar.MONDAY -> 1; Calendar.TUESDAY -> 2; Calendar.WEDNESDAY -> 3
        Calendar.THURSDAY -> 4; Calendar.FRIDAY -> 5; Calendar.SATURDAY -> 6
        Calendar.SUNDAY -> 7; else -> 1
    }
    val weekNumber = getCurrentWeekNumber()
    return CurrentDayInfo(weekNumber = weekNumber, dayNumber = dayNumber)
}

fun getCurrentWeekNumber(): Int {
    val cal = Calendar.getInstance()
    cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    val now = Calendar.getInstance()
    val diffMs = now.timeInMillis - cal.timeInMillis
    val daysDiff = (diffMs / (1000 * 60 * 60 * 24)).toInt()
    val weekNumber = (daysDiff / 7) + 1
    return weekNumber.coerceIn(1, 7)
}
