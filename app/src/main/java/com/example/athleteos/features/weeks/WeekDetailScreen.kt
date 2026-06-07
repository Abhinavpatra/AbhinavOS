package com.example.athleteos.features.weeks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.athleteos.NavigationKeys
import com.example.athleteos.NavigationState
import com.example.athleteos.database.WorkoutLog
import com.example.athleteos.features.home.BottomNavBar
import com.example.athleteos.theme.*
import com.example.athleteos.ui.AppLogo

@Composable
fun WeekDetailScreen(
    weekNumber: Int,
    onItemClick: (NavKey) -> Unit,
    onBack: () -> Unit,
    viewModel: WeekDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(weekNumber) {
        viewModel.loadWeek(weekNumber)
    }

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\u2190", color = ElectricBlue, fontSize = 22.sp, modifier = Modifier.clickable(onClick = onBack).padding(8.dp))
                AppLogo(size = 28.dp)
                Spacer(Modifier.width(8.dp))
                Text("WEEK $weekNumber", color = ElectricBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onItemClick(NavigationKeys.Home) }) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = ElectricBlue, modifier = Modifier.size(22.dp))
                }
            }

            Spacer(Modifier.height(8.dp))

            if (!state.isLoading) {
                Text("Progress: ${state.overallProgress}%", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { state.overallProgress / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (state.overallProgress == 100) SuccessGreen else ElectricBlue,
                    trackColor = CardSurface
                )

                Spacer(Modifier.height(16.dp))

                DaySelectorRow(
                    workouts = state.workouts,
                    onDayClick = { workout ->
                        NavigationState.selectedWorkoutId = workout.id
                        onItemClick(NavigationKeys.WorkoutDay)
                    }
                )

                Spacer(Modifier.height(14.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(state.workouts) { workout ->
                        WorkoutDayCard(
                            workout = workout,
                            onClick = {
                                NavigationState.selectedWorkoutId = workout.id
                                onItemClick(NavigationKeys.WorkoutDay)
                            }
                        )
                    }
                }
            }
        }

        BottomNavBar(currentRoute = "weeks", onItemClick = onItemClick)
    }
}

@Composable
private fun DaySelectorRow(
    workouts: List<WorkoutLog>,
    onDayClick: (WorkoutLog) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val sorted = workouts.sortedBy { it.dayNumber }
        sorted.forEach { workout ->
            val isComplete = workout.isCompleted
            val containerColor = if (isComplete) SuccessGreen else CardSurface
            val contentColor = if (isComplete) NearBlack else TextPrimary

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onDayClick(workout) },
                color = containerColor,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Day ${workout.dayNumber}",
                        color = contentColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        getDayName(workout.dayNumber).take(3),
                        color = if (isComplete) NearBlack.copy(alpha = 0.7f) else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutDayCard(workout: WorkoutLog, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (workout.isCompleted) SuccessGreen.copy(alpha = 0.1f) else CardSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(getDayName(workout.dayNumber), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Text(workout.name, color = TextSecondary, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${workout.completionPercentage}%", color = if (workout.isCompleted) SuccessGreen else WarningAmber, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("${workout.estimatedDurationMinutes} min", color = TextTertiary, fontSize = 12.sp)
            }
        }
        LinearProgressIndicator(
            progress = { workout.completionPercentage / 100f },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 12.dp).height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = if (workout.isCompleted) SuccessGreen else ElectricBlue,
            trackColor = CardSurfaceVariant
        )
    }
}

private fun getDayName(dayNumber: Int): String = when (dayNumber) {
    1 -> "Monday"; 2 -> "Tuesday"; 3 -> "Wednesday"; 4 -> "Thursday"
    5 -> "Friday"; 6 -> "Saturday"; 7 -> "Sunday"; else -> ""
}



