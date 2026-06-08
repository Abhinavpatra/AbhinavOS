package com.example.athleteos.features.weeks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.athleteos.features.home.BottomNavBar
import com.example.athleteos.theme.*
import com.example.athleteos.ui.AppLogo

@Composable
fun WeeksScreen(
    onItemClick: (NavKey) -> Unit,
    onBack: () -> Unit,
    viewModel: WeeksViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp).padding(top = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AppLogo(size = 28.dp)
                Spacer(Modifier.width(10.dp))
                Text("WEEKS", color = ElectricBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
            }
            Spacer(Modifier.height(20.dp))

            if (state.isLoading) return

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
                items(state.weeks) { week ->
                    WeekCard(
                        week = week,
                        onClick = {
                            NavigationState.selectedWeekNumber = week.weekNumber
                            onItemClick(NavigationKeys.WeekDetail)
                        }
                    )
                }
            }
        }

        BottomNavBar(currentRoute = "weeks", onItemClick = onItemClick)
    }
}

@Composable
private fun WeekCard(week: WeekSummary, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (week.completionPercentage == 100) SuccessGreen.copy(alpha = 0.06f)
            else if (week.isCurrentWeek) ElectricBlue.copy(alpha = 0.04f)
            else CardSurface
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (week.isCurrentWeek) ElectricBlue.copy(alpha = 0.3f) else DividerColor)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Week ${week.weekNumber}", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("${week.totalDuration} min total", color = TextSecondary, fontSize = 14.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${week.completionPercentage}%", color = if (week.completionPercentage == 100) SuccessGreen else TextSecondary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                if (week.isCurrentWeek) {
                    Spacer(Modifier.height(2.dp))
                    Text("Current", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        LinearProgressIndicator(
            progress = { week.completionPercentage / 100f },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp).height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = if (week.completionPercentage == 100) SuccessGreen else ElectricBlue,
            trackColor = CardSurfaceVariant
        )
    }
}
