package com.example.athleteos.features.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
fun HomeScreen(
    onItemClick: (NavKey) -> Unit,
    onBack: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    if (state.isLoading) return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                AppLogo(size = 32.dp)
                Spacer(Modifier.width(10.dp))
                Text(
                    "ATHLETEOS",
                    color = ElectricBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 5.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            val today = state.todayWorkout
            if (today != null) {
                Text(
                    "Today",
                    color = TextTertiary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(6.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            NavigationState.selectedWorkoutId = today.id
                            onItemClick(NavigationKeys.WorkoutDay)
                        },
                    colors = CardDefaults.cardColors(containerColor = CardSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            getDayName(today.dayNumber),
                            color = TextPrimary,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(2.dp))
                        Text(
                            today.name,
                            color = ElectricBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Week ${state.currentWeekNumber} \u00b7 Day ${today.dayNumber} \u00b7 ${today.estimatedDurationMinutes} min",
                                color = TextTertiary,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LinearProgressIndicator(
                                progress = { today.completionPercentage / 100f },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (today.isCompleted) SuccessGreen else ElectricBlue,
                                trackColor = CardSurfaceVariant,
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "${today.completionPercentage}%",
                                color = TextSecondary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                        Button(
                            onClick = {
                                NavigationState.selectedWorkoutId = today.id
                                onItemClick(NavigationKeys.WorkoutDay)
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                if (today.isCompleted) Icons.Default.Refresh else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = NearBlack,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                if (today.isCompleted) "REVIEW TODAY" else "START WORKOUT",
                                color = NearBlack,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Quick Access",
                color = TextTertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickNavCard(
                    icon = Icons.Default.DateRange,
                    label = "Weeks",
                    onClick = { onItemClick(NavigationKeys.WeeksList) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    icon = Icons.Default.Favorite,
                    label = "Armor",
                    onClick = { onItemClick(NavigationKeys.DailyArmor) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    icon = Icons.Default.Star,
                    label = "Metrics",
                    onClick = { onItemClick(NavigationKeys.Metrics) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                QuickNavCard(
                    icon = Icons.Default.Refresh,
                    label = "History",
                    onClick = { onItemClick(NavigationKeys.History) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    icon = Icons.Default.Home,
                    label = "Today",
                    onClick = {
                        today?.let {
                            NavigationState.selectedWorkoutId = it.id
                            onItemClick(NavigationKeys.WorkoutDay)
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                QuickNavCard(
                    icon = Icons.Default.Settings,
                    label = "Settings",
                    onClick = { onItemClick(NavigationKeys.Settings) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                "Weeks",
                color = TextTertiary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(10.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.weeks, key = { it.weekNumber }) { week ->
                    WeekRow(
                        week = week,
                        onClick = {
                            NavigationState.selectedWeekNumber = week.weekNumber
                            onItemClick(NavigationKeys.WeekDetail)
                        }
                    )
                }
            }
        }

        BottomNavBar(
            currentRoute = "home",
            onItemClick = onItemClick
        )
    }
}

@Composable
private fun QuickNavCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = CardSurface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = ElectricBlue,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun WeekRow(week: WeekSummary, onClick: () -> Unit) {
    val isCurrent = week.isCurrentWeek
    val isComplete = week.completionPercentage == 100

    val border = if (isCurrent) ElectricBlue.copy(alpha = 0.5f) else CardSurfaceVariant
    val accent = when {
        isComplete -> SuccessGreen
        isCurrent -> ElectricBlue
        week.completionPercentage > 0 -> WarningAmber
        else -> TextTertiary
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = if (isCurrent) CardSurface else Color.Transparent,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "Week ${week.weekNumber}",
                    color = when {
                        isCurrent || isComplete || week.completionPercentage > 0 -> TextPrimary
                        else -> TextSecondary
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isCurrent) {
                    Spacer(Modifier.width(8.dp))
                    Surface(
                        color = ElectricBlue.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "NOW",
                            color = ElectricBlue,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.Bottom) {
                if (isComplete) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Complete",
                        tint = SuccessGreen,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        "${week.completionPercentage}",
                        color = accent,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "%",
                        color = accent.copy(alpha = 0.5f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
internal fun BottomNavBar(
    currentRoute: String,
    onItemClick: (NavKey) -> Unit
) {
    Surface(
        color = DarkGray,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem("Home", Icons.Default.Home, currentRoute == "home") { onItemClick(NavigationKeys.Home) }
            BottomNavItem("Weeks", Icons.Default.DateRange, currentRoute == "weeks") { onItemClick(NavigationKeys.WeeksList) }
            BottomNavItem("Armor", Icons.Default.Favorite, currentRoute == "armor") { onItemClick(NavigationKeys.DailyArmor) }
            BottomNavItem("Metrics", Icons.Default.Star, currentRoute == "metrics") { onItemClick(NavigationKeys.Metrics) }
            BottomNavItem("History", Icons.Default.Refresh, currentRoute == "history") { onItemClick(NavigationKeys.History) }
            BottomNavItem("Settings", Icons.Default.Settings, currentRoute == "settings") { onItemClick(NavigationKeys.Settings) }
        }
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = if (isSelected) ElectricBlue else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = if (isSelected) ElectricBlue else TextSecondary,
            fontSize = 9.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

private fun getDayName(dayNumber: Int): String = when (dayNumber) {
    1 -> "Monday"; 2 -> "Tuesday"; 3 -> "Wednesday"; 4 -> "Thursday"
    5 -> "Friday"; 6 -> "Saturday"; 7 -> "Sunday"; else -> ""
}
