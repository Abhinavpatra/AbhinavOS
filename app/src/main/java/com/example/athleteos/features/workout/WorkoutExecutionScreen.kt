package com.example.athleteos.features.workout

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.athleteos.NavigationKeys
import com.example.athleteos.database.ExerciseLog
import com.example.athleteos.database.ExerciseSetLog
import com.example.athleteos.theme.*
import com.example.athleteos.ui.AppLogo
import kotlinx.coroutines.delay

@Composable
fun WorkoutExecutionScreen(
    workoutId: Long,
    onItemClick: (NavKey) -> Unit,
    onBack: () -> Unit,
    viewModel: WorkoutExecutionViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(workoutId) {
        viewModel.loadWorkout(workoutId)
    }

    LaunchedEffect(state.isRestTimerRunning) {
        if (state.isRestTimerRunning) {
            while (state.restTimerSeconds > 0) {
                delay(1000L)
                viewModel.tickRestTimer()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\u2190", color = ElectricBlue, fontSize = 22.sp, modifier = Modifier.clickable(onClick = onBack).padding(8.dp))
                AppLogo(size = 28.dp)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val workout = state.workout
                    if (workout != null) {
                        Text(getDayName(workout.dayNumber), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(workout.name, color = ElectricBlue, fontSize = 14.sp)
                    }
                }
                IconButton(onClick = { onItemClick(NavigationKeys.Home) }) {
                    Icon(Icons.Default.Home, contentDescription = "Home", tint = ElectricBlue)
                }
            }

            Spacer(Modifier.height(12.dp))

            val workout = state.workout
            if (workout != null) {
                LinearProgressIndicator(
                    progress = { workout.completionPercentage / 100f },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = if (workout.isCompleted) SuccessGreen else ElectricBlue,
                    trackColor = CardSurface
                )
                Spacer(Modifier.height(4.dp))
                Text("${workout.completionPercentage}% complete", color = TextSecondary, fontSize = 12.sp)
            }

            Spacer(Modifier.height(14.dp))

            if (!state.isLoading) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                    items(state.exercises) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            sets = state.exerciseSets[exercise.id] ?: emptyList(),
                            isExpanded = state.expandedExerciseId == exercise.id,
                            onToggleExpand = { viewModel.toggleExerciseExpanded(exercise.id) },
                            onCompleteSet = { set -> viewModel.completeSet(set) },
                            onWeightChange = { set, w -> viewModel.updateActualWeight(set, w) },
                            onRepsChange = { set, r -> viewModel.updateActualReps(set, r) },
                            onCompleteExercise = { viewModel.completeExercise(exercise.id) },
                            onUndo = { viewModel.undoLastCompletion() }
                        )
                    }
                }
            }
        }

        if (state.isRestTimerRunning) {
            RestTimerOverlay(
                seconds = state.restTimerSeconds,
                onStop = { viewModel.stopRestTimer() },
                onSetTimer = { viewModel.setRestTimer(it) }
            )
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseLog,
    sets: List<ExerciseSetLog>,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCompleteSet: (ExerciseSetLog) -> Unit,
    onWeightChange: (ExerciseSetLog, Double) -> Unit,
    onRepsChange: (ExerciseSetLog, Int) -> Unit,
    onCompleteExercise: () -> Unit,
    onUndo: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onToggleExpand,
                onDoubleClick = onCompleteExercise,
                onLongClick = onUndo
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (exercise.isCompleted) SuccessGreen.copy(alpha = 0.1f) else CardSurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(20.dp).clip(CircleShape).background(if (exercise.isCompleted) SuccessGreen else CardSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        if (exercise.isCompleted) Text("\u2713", color = NearBlack, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(exercise.name, color = if (exercise.isCompleted) SuccessGreen else TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, textDecoration = if (exercise.isCompleted) TextDecoration.LineThrough else TextDecoration.None)
                }
                Text(if (isExpanded) "\u25B2" else "\u25BC", color = TextSecondary, fontSize = 12.sp)
            }

            if (sets.isNotEmpty()) {
                val targetDisplay = sets.first().let { s ->
                    buildString {
                        if (s.targetWeight != null) append("${formatWeight(s.targetWeight)}kg \u00d7 ")
                        if (s.targetReps != null) append("${s.targetReps}")
                        append(" \u00b7 Rest: ${s.targetRestSeconds}s")
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text("Target: $targetDisplay", color = TextSecondary, fontSize = 12.sp)
            }

            val completedCount = sets.count { it.isCompleted }
            if (completedCount > 0) {
                Spacer(Modifier.height(4.dp))
                Text("$completedCount/${sets.size} sets completed", color = SuccessGreen, fontSize = 12.sp)
            }

            if (isExpanded && sets.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                sets.forEach { set ->
                    SetRow(set = set, onComplete = { onCompleteSet(set) }, onWeightChange = { w -> onWeightChange(set, w) }, onRepsChange = { r -> onRepsChange(set, r) })
                }
            }
        }
    }
}

@Composable
private fun SetRow(set: ExerciseSetLog, onComplete: () -> Unit, onWeightChange: (Double) -> Unit, onRepsChange: (Int) -> Unit) {
    var weightText by remember(set.actualWeight) { mutableStateOf(set.actualWeight?.let { formatWeight(it) } ?: "") }
    var repsText by remember(set.actualReps) { mutableStateOf(set.actualReps?.toString() ?: "") }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Set ${set.setNumber}", color = TextSecondary, fontSize = 13.sp, modifier = Modifier.width(48.dp))
        Spacer(Modifier.width(4.dp))

        Text("Target:", color = TextTertiary, fontSize = 11.sp)
        Text(
            buildString {
                if (set.targetWeight != null) append("${formatWeight(set.targetWeight)}kg \u00d7 ")
                if (set.targetReps != null) append("${set.targetReps}")
            },
            color = TextSecondary, fontSize = 12.sp, modifier = Modifier.width(80.dp)
        )

        OutlinedTextField(
            value = weightText,
            onValueChange = { weightText = it; it.toDoubleOrNull()?.let(onWeightChange) },
            modifier = Modifier.width(60.dp).height(40.dp),
            placeholder = { Text("kg", color = TextTertiary, fontSize = 11.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = TextPrimary),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = CardSurfaceVariant,
                cursorColor = ElectricBlue
            )
        )
        Spacer(Modifier.width(4.dp))
        Text("\u00d7", color = TextSecondary, fontSize = 12.sp)
        Spacer(Modifier.width(4.dp))
        OutlinedTextField(
            value = repsText,
            onValueChange = { repsText = it; it.toIntOrNull()?.let(onRepsChange) },
            modifier = Modifier.width(48.dp).height(40.dp),
            placeholder = { Text("reps", color = TextTertiary, fontSize = 11.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp, color = TextPrimary),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = CardSurfaceVariant,
                cursorColor = ElectricBlue
            )
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (set.isCompleted) SuccessGreen else CardSurfaceVariant)
                .clickable(onClick = onComplete),
            contentAlignment = Alignment.Center
        ) {
            if (set.isCompleted) Text("\u2713", color = NearBlack, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RestTimerOverlay(seconds: Int, onStop: () -> Unit, onSetTimer: (Int) -> Unit) {
    val presets = listOf(30, 60, 90, 120, 180)
    Box(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)).clickable(onClick = {}),
        contentAlignment = Alignment.TopEnd
    ) {
        Card(
            modifier = Modifier.padding(16.dp).width(200.dp),
            colors = CardDefaults.cardColors(containerColor = DarkGray),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Text("REST", color = TextSecondary, fontSize = 12.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(4.dp))
                val minutes = seconds / 60
                val secs = seconds % 60
                Text(
                    "${minutes}:${secs.toString().padStart(2, '0')}",
                    color = ElectricBlue,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    presets.forEach { p ->
                        Text(
                            "$p",
                            color = if (seconds == p) ElectricBlue else TextSecondary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (seconds == p) ElectricBlue.copy(alpha = 0.2f) else CardSurfaceVariant)
                                .clickable { onSetTimer(p) }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onStop,
                    colors = ButtonDefaults.buttonColors(containerColor = FailureRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("STOP", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun formatWeight(weight: Double): String = if (weight == weight.toLong().toDouble()) weight.toLong().toString() else String.format("%.1f", weight)

private fun getDayName(dayNumber: Int): String = when (dayNumber) {
    1 -> "Monday"; 2 -> "Tuesday"; 3 -> "Wednesday"; 4 -> "Thursday"
    5 -> "Friday"; 6 -> "Saturday"; 7 -> "Sunday"; else -> ""
}
