package com.example.athleteos.features.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.athleteos.NavigationKeys
import com.example.athleteos.NavigationState
import com.example.athleteos.features.home.BottomNavBar
import com.example.athleteos.theme.*
import com.example.athleteos.ui.AppLogo
import java.util.Calendar

@Composable
fun HistoryScreen(
    onItemClick: (NavKey) -> Unit,
    onBack: () -> Unit,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp).padding(top = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\u2190", color = ElectricBlue, fontSize = 22.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 8.dp))
                AppLogo(size = 28.dp)
                Spacer(Modifier.width(10.dp))
                Text("HISTORY", color = ElectricBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("\u25C0", color = ElectricBlue, fontSize = 18.sp, modifier = Modifier.clickable { viewModel.previousMonth() }.padding(8.dp))
                Text(
                    "${getMonthName(state.currentMonth)} ${state.currentYear}",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text("\u25B6", color = ElectricBlue, fontSize = 18.sp, modifier = Modifier.clickable { viewModel.nextMonth() }.padding(8.dp))
            }

            Spacer(Modifier.height(12.dp))

            val dayNames = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                dayNames.forEach { Text(it, color = TextTertiary, fontSize = 11.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(40.dp), textAlign = TextAlign.Center) }
            }

            Spacer(Modifier.height(4.dp))

            val cal = Calendar.getInstance().apply { set(state.currentYear, state.currentMonth, 1) }
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - Calendar.MONDAY + 7) % 7
            val totalCells = firstDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val day = cellIndex - firstDayOfWeek + 1
                            if (day in 1..daysInMonth) {
                                val dateKey = String.format("%04d-%02d-%02d", state.currentYear, state.currentMonth + 1, day)
                                val status = state.dayStatuses[dateKey] ?: WorkoutStatus.EMPTY
                                val color = when (status) {
                                    WorkoutStatus.COMPLETED -> SuccessGreen
                                    WorkoutStatus.PARTIAL -> WarningAmber
                                    WorkoutStatus.MISSED -> FailureRed
                                    WorkoutStatus.EMPTY -> CardSurfaceVariant
                                }
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(if (status == WorkoutStatus.EMPTY) CardSurface else color.copy(alpha = 0.15f))
                                        .clickable { viewModel.selectDay(day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$day",
                                        color = if (status == WorkoutStatus.EMPTY) TextTertiary else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (status == WorkoutStatus.EMPTY) FontWeight.Normal else FontWeight.Medium
                                    )
                                }
                            } else {
                                Spacer(Modifier.size(40.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                listOf(
                    "COMPLETED",
                    "PARTIAL",
                    "MISSED"
                ).forEach { label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(
                                    when (label) {
                                        "COMPLETED" -> SuccessGreen
                                        "PARTIAL" -> WarningAmber
                                        "MISSED" -> FailureRed
                                        else -> CardSurfaceVariant
                                    }
                                )
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(label, color = TextSecondary, fontSize = 10.sp)
                    }
                }
            }

            if (state.selectedDayWorkouts.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text("Selected Day Workouts", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.selectedDayWorkouts) { workout ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                NavigationState.selectedWorkoutId = workout.id
                                onItemClick(NavigationKeys.WorkoutDay)
                            },
                            colors = CardDefaults.cardColors(containerColor = CardSurface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(getDayName(workout.dayNumber), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                    Text(workout.name, color = TextSecondary, fontSize = 14.sp)
                                }
                                Text(
                                    if (workout.isCompleted) "COMPLETE" else "${workout.completionPercentage}%",
                                    color = if (workout.isCompleted) SuccessGreen else WarningAmber,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        BottomNavBar(currentRoute = "history", onItemClick = onItemClick)
    }
}

private fun getMonthName(month: Int): String = when (month) {
    0 -> "January"; 1 -> "February"; 2 -> "March"; 3 -> "April"
    4 -> "May"; 5 -> "June"; 6 -> "July"; 7 -> "August"
    8 -> "September"; 9 -> "October"; 10 -> "November"; 11 -> "December"
    else -> ""
}

private fun getDayName(dayNumber: Int): String = when (dayNumber) {
    1 -> "Monday"; 2 -> "Tuesday"; 3 -> "Wednesday"; 4 -> "Thursday"
    5 -> "Friday"; 6 -> "Saturday"; 7 -> "Sunday"; else -> ""
}
