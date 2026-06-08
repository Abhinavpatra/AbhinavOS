package com.example.athleteos.features.armor

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.athleteos.features.home.BottomNavBar
import com.example.athleteos.theme.*
import com.example.athleteos.ui.AppLogo

@Composable
fun DailyArmorScreen(
    onBack: () -> Unit,
    onItemClick: (NavKey) -> Unit,
    viewModel: DailyArmorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp).padding(top = 6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("\u2190", color = ElectricBlue, fontSize = 22.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 8.dp))
                AppLogo(size = 28.dp)
                Spacer(Modifier.width(10.dp))
                Text("DAILY ARMOR", color = ElectricBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StreakBox("CURRENT", state.currentStreak, ElectricBlue)
                StreakBox("BEST", state.longestStreak, SuccessGreen)
            }

            Spacer(Modifier.height(20.dp))

            if (state.isTodayCompleted) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.06f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, SuccessGreen.copy(alpha = 0.3f))
                ) {
                    Text(
                        "Today's armor is complete! \u2713",
                        color = SuccessGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(Modifier.height(12.dp))
            }

            if (state.isLoading) return

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                items(state.armorItems) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { viewModel.toggleItem(item.name) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.isCompleted) SuccessGreen.copy(alpha = 0.06f) else CardSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (item.isCompleted) SuccessGreen.copy(alpha = 0.2f) else DividerColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (item.isCompleted) SuccessGreen else CardSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                if (item.isCompleted) {
                                    Text("\u2713", color = CardSurface, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(
                                item.name,
                                color = if (item.isCompleted) SuccessGreen else TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        BottomNavBar(currentRoute = "armor", onItemClick = onItemClick)
    }
}

@Composable
private fun StreakBox(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(140.dp),
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(label, color = TextTertiary, fontSize = 10.sp, letterSpacing = 2.sp)
            Spacer(Modifier.height(4.dp))
            Text("$value", color = color, fontSize = 36.sp, fontWeight = FontWeight.Bold)
            Text("days", color = TextSecondary, fontSize = 12.sp)
        }
    }
}
