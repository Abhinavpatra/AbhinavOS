package com.example.athleteos.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.athleteos.theme.*
import com.example.athleteos.ui.AppLogo

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 16.dp).padding(top = 6.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\u2190", color = ElectricBlue, fontSize = 22.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 8.dp))
            AppLogo(size = 28.dp)
            Spacer(Modifier.width(10.dp))
            Text("SETTINGS", color = ElectricBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
        }

        Spacer(Modifier.height(24.dp))

        SettingsSection("DISPLAY") {
            SettingsToggle(
                label = "Dark Mode",
                description = if (state.darkMode == null) "Follow system" else if (state.darkMode == true) "Always dark" else "Always light",
                checked = state.darkMode == true,
                onCheckedChange = { viewModel.setDarkMode(if (it) true else null) }
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSection("NOTIFICATIONS") {
            SettingsToggle(
                label = "Training Reminders",
                description = "6:00 PM teaser & 9:00 PM reminder",
                checked = state.notificationsEnabled,
                onCheckedChange = { viewModel.setNotificationsEnabled(it) }
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSection("REST TIMER") {
            val presets = listOf(30, 60, 90, 120, 180)
            Text("Default: ${state.restTimerDefault}s", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                presets.forEach { sec ->
                    Text(
                        "$sec",
                        modifier = Modifier
                            .clickable { viewModel.setRestTimerDefault(sec) }
                            .background(
                                if (state.restTimerDefault == sec) ElectricBlue.copy(alpha = 0.2f) else CardSurfaceVariant,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (state.restTimerDefault == sec) ElectricBlue else TextSecondary,
                        fontSize = 14.sp,
                        fontWeight = if (state.restTimerDefault == sec) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        SettingsSection("UNITS") {
            SettingsToggle(
                label = "Metric Units",
                description = if (state.isMetric) "kg, cm, meters" else "lbs, inches, feet",
                checked = state.isMetric,
                onCheckedChange = { viewModel.setIsMetric(it) }
            )
        }

        Spacer(Modifier.height(16.dp))

        SettingsSection("DATA") {
            Button(
                onClick = { viewModel.exportData() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Export Data", color = TextPrimary, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.importData() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CardSurfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Import Data", color = TextPrimary, fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = { viewModel.clearAllData() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = FailureRed.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Clear All Data", color = FailureRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(title, color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(Modifier.height(8.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp), content = content)
        }
    }
}

@Composable
private fun SettingsToggle(label: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(description, color = TextTertiary, fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedTrackColor = ElectricBlue, checkedThumbColor = NearBlack, uncheckedTrackColor = CardSurfaceVariant)
        )
    }
}
