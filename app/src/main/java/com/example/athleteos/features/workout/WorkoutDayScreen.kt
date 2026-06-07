package com.example.athleteos.features.workout

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.athleteos.NavigationKeys
import com.example.athleteos.NavigationState
import com.example.athleteos.theme.*
import com.example.athleteos.ui.AppLogo

@Composable
fun WorkoutDayScreen(
    workoutId: Long,
    onItemClick: (NavKey) -> Unit,
    onBack: () -> Unit,
    viewModel: WorkoutDayViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\u2190", color = ElectricBlue, fontSize = 22.sp, modifier = Modifier.clickable(onClick = onBack).padding(8.dp))
                AppLogo(size = 28.dp)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val workout = state.workout
                    if (workout != null) {
                        Text(getDayName(workout.dayNumber), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(workout.name, color = ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
                IconButton(onClick = { onItemClick(NavigationKeys.Home) }) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = ElectricBlue)
                }
            }

            if (state.isLoading) return

            Spacer(Modifier.height(6.dp))

            val workout = state.workout
            if (workout != null) {
                Text("Week ${workout.weekNumber} \u00b7 Day ${workout.dayNumber} \u00b7 ${workout.estimatedDurationMinutes} min", color = TextTertiary, fontSize = 13.sp)
                Spacer(Modifier.height(4.dp))
                Text("Progress: ${workout.completionPercentage}%", color = TextSecondary, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { workout.completionPercentage / 100f },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = if (workout.isCompleted) SuccessGreen else ElectricBlue,
                    trackColor = CardSurface
                )
            }

            Spacer(Modifier.height(10.dp))

            DayNavigationRow(
                currentWorkout = workout,
                weekWorkouts = state.weekWorkouts,
                onDayClick = { targetWorkout ->
                    NavigationState.selectedWorkoutId = targetWorkout.id
                    viewModel.loadWorkout(targetWorkout.id)
                }
            )

            Spacer(Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.exercises) { exercise ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (exercise.isCompleted) SuccessGreen.copy(alpha = 0.1f) else CardSurface
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (exercise.isCompleted) SuccessGreen else CardSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (exercise.isCompleted) {
                                    Text("\u2713", color = NearBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                exercise.name,
                                color = if (exercise.isCompleted) SuccessGreen else TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                textDecoration = if (exercise.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { onItemClick(NavigationKeys.WorkoutExecution) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (workout?.isCompleted == true) "REVIEW EXECUTION" else "START EXECUTION",
                    color = NearBlack, fontSize = 17.sp, fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DayNavigationRow(
    currentWorkout: com.example.athleteos.database.WorkoutLog?,
    weekWorkouts: List<com.example.athleteos.database.WorkoutLog>,
    onDayClick: (com.example.athleteos.database.WorkoutLog) -> Unit
) {
    if (currentWorkout == null) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val sorted = weekWorkouts.sortedBy { it.dayNumber }
        sorted.forEach { workout ->
            val isSelected = workout.id == currentWorkout.id
            val isComplete = workout.isCompleted
            val bgColor = when {
                isSelected -> ElectricBlue
                isComplete -> SuccessGreen.copy(alpha = 0.3f)
                else -> CardSurface
            }
            val textColor = if (isSelected) NearBlack else TextPrimary

            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onDayClick(workout) },
                color = bgColor,
                shape = RoundedCornerShape(10.dp),
                border = if (!isSelected) BorderStroke(1.dp, CardSurfaceVariant) else null
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Day ${workout.dayNumber}",
                        color = textColor,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        getDayName(workout.dayNumber).take(3),
                        color = if (isSelected) NearBlack.copy(alpha = 0.7f) else TextTertiary,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun getDayName(dayNumber: Int): String = when (dayNumber) {
    1 -> "Monday"; 2 -> "Tuesday"; 3 -> "Wednesday"; 4 -> "Thursday"
    5 -> "Friday"; 6 -> "Saturday"; 7 -> "Sunday"; else -> ""
}
