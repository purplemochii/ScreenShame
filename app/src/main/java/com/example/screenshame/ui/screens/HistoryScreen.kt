package com.example.screenshame.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.screenshame.ui.theme.*
import com.example.screenshame.ui.viewmodel.ScreenShameViewModel
import com.example.screenshame.data.repository.AppUsageSummary
import com.example.screenshame.ui.components.BottomNav

@Composable
fun HistoryScreen(navController: NavController, vm: ScreenShameViewModel = viewModel()) {
    val state by vm.historyState.collectAsState()

    LaunchedEffect(Unit) { vm.loadHistory() }

    Scaffold(
        bottomBar = { BottomNav(navController, "history") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Text("Hall of Shame", style = MaterialTheme.typography.displayLarge, color = black)
                Text("7-Day Usage History",
                    style = MaterialTheme.typography.bodyMedium, color = textSecondary)
                Spacer(modifier = Modifier.height(20.dp))

                // weekly summary card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, border)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("Weekly Screen Time",
                                    fontWeight = FontWeight.Bold, fontSize = 15.sp, color = black)
                                Spacer(modifier = Modifier.height(4.dp))
                                val hours = state.totalWeeklyMinutes / 60
                                val mins = state.totalWeeklyMinutes % 60
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("↗", color = red, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("this week",
                                        style = MaterialTheme.typography.labelSmall, color = red)
                                }
                            }
                            val hours = state.totalWeeklyMinutes / 60
                            val mins = state.totalWeeklyMinutes % 60
                            Column(horizontalAlignment = Alignment.End) {
                                Text("${hours}h ${mins}m",
                                    fontWeight = FontWeight.Bold, fontSize = 22.sp, color = black)
                                Text("Total", style = MaterialTheme.typography.labelSmall,
                                    color = textSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // line chart
                        if (state.weeklyData.isNotEmpty()) {
                            WeeklyLineChart(data = state.weeklyData)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                Text("Worst Offenses",
                    style = MaterialTheme.typography.headlineMedium, color = black)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp),
                        contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = black, modifier = Modifier.size(24.dp))
                    }
                }
            } else if (state.worstOffenses.isEmpty()) {
                item {
                    Text("No limit breaks yet. Impressive.",
                        style = MaterialTheme.typography.bodyMedium, color = textSecondary)
                }
            } else {
                items(state.worstOffenses) { app ->
                    ShameCard(app)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun WeeklyLineChart(data: List<Pair<String, Int>>) {
    val maxVal = data.maxOfOrNull { it.second }?.toFloat() ?: 1f
    val lineColor = Color(0xFF0A0A0A)
    val fillColor = Color(0xFF0A0A0A).copy(alpha = 0.08f)
    val gridColor = Color(0xFFE5E5E5)

    Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height - 20.dp.toPx()
            val stepX = w / (data.size - 1).coerceAtLeast(1)

            // grid lines
            val gridSteps = listOf(0f, 0.25f, 0.5f, 0.75f, 1f)
            gridSteps.forEach { frac ->
                val y = h - (frac * h)
                drawLine(gridColor, Offset(0f, y), Offset(w, y), strokeWidth = 0.5.dp.toPx())
            }

            // build path
            val points = data.mapIndexed { i, (_, v) ->
                Offset(i * stepX, h - (v / maxVal * h))
            }

            // fill path
            val fillPath = Path().apply {
                moveTo(points.first().x, h)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, h)
                close()
            }
            drawPath(fillPath, fillColor)

            // line path
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(linePath, lineColor, style = Stroke(width = 2.dp.toPx()))

            // dots
            points.forEach {
                drawCircle(lineColor, radius = 3.dp.toPx(), center = it)
                drawCircle(Color.White, radius = 1.5.dp.toPx(), center = it)
            }
        }

        // day labels at bottom
        Row(
            modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEach { (label, _) ->
                Text(label, style = MaterialTheme.typography.labelSmall,
                    color = textSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
fun ShameCard(app: AppUsageSummary) {
    val roast = remember(app.packageName) {
        listOf(
            "Basically a part-time job scrolling.",
            "Your brain cells are melting.",
            "Arguing with strangers online again.",
            "Time you will never get back.",
            "This is not what productivity looks like.",
            "Your ancestors are disappointed.",
            "The algorithm won. Again."
        ).random()
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, border)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape)
                        .background(Color(0xFFFFF0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("☠", fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(app.appName, fontWeight = FontWeight.Medium,
                        fontSize = 15.sp, color = black)
                    Text("today", style = MaterialTheme.typography.labelSmall,
                        color = textSecondary)
                }
                val h = app.usageMinutes / 60
                val m = app.usageMinutes % 60
                Text(
                    if (h > 0) "${h}h ${m}m" else "${m}m",
                    fontWeight = FontWeight.Bold, fontSize = 15.sp, color = red
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(6.dp),
                color = white
            ) {
                Text(
                    text = "\"$roast\"",
                    modifier = Modifier.padding(10.dp),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = textSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}