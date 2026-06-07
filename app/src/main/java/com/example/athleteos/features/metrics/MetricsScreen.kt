package com.example.athleteos.features.metrics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.athleteos.theme.*
import com.example.athleteos.ui.AppLogo

@Composable
fun MetricsScreen(
    onBack: () -> Unit,
    viewModel: MetricsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var inputValue by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 16.dp).padding(top = 6.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("\u2190", color = ElectricBlue, fontSize = 22.sp, modifier = Modifier.clickable(onClick = onBack).padding(end = 8.dp))
            AppLogo(size = 28.dp)
            Spacer(Modifier.width(10.dp))
            Text("METRICS", color = ElectricBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold, letterSpacing = 4.sp)
        }

        Spacer(Modifier.height(16.dp))

        val pr = viewModel.getPR()
        if (pr != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = WarningAmber.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("\u2605", color = WarningAmber, fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text("PR: ${state.selectedMetric}", color = WarningAmber, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(pr, color = TextPrimary, fontSize = 14.sp)
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.categoryMetrics.keys.toList()) { category ->
                Text(
                    category,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (state.selectedCategory == category) ElectricBlue.copy(alpha = 0.2f) else CardSurface)
                        .clickable { viewModel.selectCategory(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (state.selectedCategory == category) ElectricBlue else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (state.selectedCategory == category) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        val metrics = state.categoryMetrics[state.selectedCategory] ?: emptyList()
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(metrics) { metric ->
                Text(
                    metric,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (state.selectedMetric == metric) ElectricBlue.copy(alpha = 0.2f) else CardSurface)
                        .clickable { viewModel.selectMetric(metric) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    color = if (state.selectedMetric == metric) ElectricBlue else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = if (state.selectedMetric == metric) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("7d", "30d", "90d", "all").forEach { range ->
                Text(
                    range.uppercase(),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (state.selectedTimeRange == range) ElectricBlue.copy(alpha = 0.2f) else CardSurface)
                        .clickable { viewModel.selectTimeRange(range) }
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    color = if (state.selectedTimeRange == range) ElectricBlue else TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(200.dp)
        ) {
            if (state.metricHistory.isNotEmpty()) {
                val values = state.metricHistory.map { it.value }
                val labels = state.metricHistory.map { it.date.substringAfterLast("-") }
                LineChart(values = values, labels = labels, modifier = Modifier.fillMaxSize().padding(16.dp))
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No data yet. Add your first entry below.", color = TextTertiary, fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CardSurface),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Log ${state.selectedMetric}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = inputValue,
                        onValueChange = { inputValue = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Value", color = TextTertiary) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        textStyle = LocalTextStyle.current.copy(color = TextPrimary),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue, unfocusedBorderColor = CardSurfaceVariant, cursorColor = ElectricBlue)
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            inputValue.toDoubleOrNull()?.let {
                                viewModel.saveMetricValue(it)
                                inputValue = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("LOG", color = NearBlack, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LineChart(values: List<Double>, labels: List<String>, modifier: Modifier = Modifier) {
    if (values.isEmpty()) return
    val maxValue = values.max() + 1
    val minValue = (values.min() - 1).coerceAtMost(0.0)
    val range = maxValue - minValue

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val stepX = if (values.size > 1) width / (values.size - 1) else width

        val path = Path()
        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - minValue) / range * height).toFloat()
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }

        drawPath(path, color = ElectricBlue, style = Stroke(width = 3f))

        values.forEachIndexed { index, value ->
            val x = index * stepX
            val y = height - ((value - minValue) / range * height).toFloat()
            drawCircle(color = ElectricBlue, radius = 4f, center = Offset(x, y))
        }
    }
}
