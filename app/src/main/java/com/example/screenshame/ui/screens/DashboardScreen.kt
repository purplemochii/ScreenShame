package com.example.screenshame.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.screenshame.data.repository.AppUsageSummary
import com.example.screenshame.ui.theme.*
import com.example.screenshame.ui.viewmodel.ScreenShameViewModel
import androidx.compose.ui.graphics.Color
import com.example.screenshame.ui.components.BottomNav

@Composable
fun DashboardScreen(navController: NavController, vm: ScreenShameViewModel = viewModel()) {
    val state by vm.dashboardState.collectAsState()

    LaunchedEffect(Unit) { vm.loadDashboard() }

    Scaffold(
        bottomBar = { BottomNav(navController, "dashboard") }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Today",
                            style = MaterialTheme.typography.displayLarge,
                            color = black
                        )
                        Text(
                            text = "UsageStatsManager Sync Active",
                            style = MaterialTheme.typography.labelSmall,
                            color = textSecondary
                        )
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Settings, contentDescription = "settings", tint = textSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // roast card
            if (state.roastMessage.isNotEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = black
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.Warning,
                                    contentDescription = null,
                                    tint = red,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "[ALERT: SYSTEM SHAME]",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    color = red,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.roastMessage,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 14.sp,
                                color = white,
                                lineHeight = 22.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // app usage header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "App Usage",
                        style = MaterialTheme.typography.headlineMedium,
                        color = black
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = null,
                            tint = textSecondary, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Live from OS", style = MaterialTheme.typography.labelSmall, color = textSecondary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = black, modifier = Modifier.size(24.dp))
                    }
                }
            } else {
                items(state.appUsage.filter { it.limitMinutes != null && it.limitMinutes > 0 }) { app ->
                    AppUsageRow(app)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // minutes stolen card
                if (state.totalMinutesOverLimit > 0) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFF0F0)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "-${state.totalMinutesOverLimit}",
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = red,
                                    fontFamily = FontFamily.Serif
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("Minutes Stolen", fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp, color = black)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "You are collectively ${state.totalMinutesOverLimit} minutes over your set daily limits. The Room DB remembers everything.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = textSecondary,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun AppUsageRow(app: AppUsageSummary) {
    val isOver = app.isOverLimit
    val usageColor = if (isOver) red else black
    val limit = app.limitMinutes ?: 0
    val progress = if (limit > 0) (app.usageMinutes.toFloat() / limit).coerceIn(0f, 1f) else 0f

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // app icon placeholder circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(surface),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.appName.take(1),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(app.appName, fontWeight = FontWeight.Medium, fontSize = 15.sp, color = black)
                Text(
                    text = "${app.usageMinutes}m / ${limit}m limit",
                    style = MaterialTheme.typography.labelSmall,
                    color = usageColor
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        // usage bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(border)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isOver) red else black)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}