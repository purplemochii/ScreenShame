package com.example.screenshame.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.screenshame.data.db.AppLimit
import com.example.screenshame.ui.components.AppIconCircle
import com.example.screenshame.ui.components.BottomNav
import com.example.screenshame.ui.theme.*
import com.example.screenshame.ui.viewmodel.ScreenShameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LimitsScreen(navController: NavController, vm: ScreenShameViewModel = viewModel()) {
    val state by vm.limitsState.collectAsState()
    var showAddSheet by remember { mutableStateOf(false) }
    var editingApp by remember { mutableStateOf<AppLimit?>(null) }
    var sliderValue by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) { vm.loadLimits() }

    Scaffold(
        bottomBar = { BottomNav(navController, "limits") },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = black,
                contentColor = white,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Outlined.Add, contentDescription = "add app")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Text("Set Limits", style = MaterialTheme.typography.displayLarge, color = black)
                Text("Configure your pain thresholds.",
                    style = MaterialTheme.typography.bodyMedium, color = textSecondary)
                Spacer(modifier = Modifier.height(20.dp))

                // zero mercy policy banner
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = black
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.Shield, contentDescription = null,
                            tint = white, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Zero Mercy Policy", fontWeight = FontWeight.Bold,
                                fontSize = 15.sp, color = white)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Once a limit is set, the app will actively shame you when exceeded. There is no snoozing.",
                                style = MaterialTheme.typography.labelSmall,
                                color = white.copy(alpha = 0.7f),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            items(state.trackedApps) { app ->
                // if this app is being edited show the slider inline
                if (editingApp?.packageName == app.packageName) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIconCircle(app.appName)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(app.appName, fontWeight = FontWeight.Medium,
                                    fontSize = 15.sp, color = black)
                                Text("${sliderValue.toInt()} minutes/day",
                                    style = MaterialTheme.typography.labelSmall, color = textSecondary)
                            }
                            TextButton(onClick = { editingApp = null }) {
                                Text("Cancel", color = textSecondary)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Button(
                                onClick = {
                                    vm.setLimit(app.packageName, app.appName, sliderValue.toInt())
                                    editingApp = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = black),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("Save") }
                        }

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = surface
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Daily Limit", fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp, color = black)
                                    Text("${sliderValue.toInt()} min",
                                        fontWeight = FontWeight.Bold, fontSize = 14.sp, color = red)
                                }
                                Slider(
                                    value = sliderValue,
                                    onValueChange = { sliderValue = it },
                                    valueRange = 0f..240f,
                                    colors = SliderDefaults.colors(
                                        thumbColor = black,
                                        activeTrackColor = black,
                                        inactiveTrackColor = border
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    listOf("0m", "1h", "2h", "3h", "4h+").forEach {
                                        Text(it, style = MaterialTheme.typography.labelSmall,
                                            color = textSecondary)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AppIconCircle(app.appName)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(app.appName, fontWeight = FontWeight.Medium,
                                fontSize = 15.sp, color = black)
                            Text(
                                if (app.dailyLimitMinutes > 0) "${app.dailyLimitMinutes} minutes/day"
                                else "No limit set",
                                style = MaterialTheme.typography.labelSmall, color = textSecondary
                            )
                        }
                        OutlinedButton(
                            onClick = {
                                editingApp = app
                                sliderValue = app.dailyLimitMinutes.toFloat()
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(border)
                            )
                        ) {
                            Text("Edit", color = black, fontSize = 13.sp)
                        }
                    }
                    HorizontalDivider(color = border, thickness = 0.5.dp)
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // add app bottom sheet
    if (showAddSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAddSheet = false },
            containerColor = white
        ) {
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Add an app", style = MaterialTheme.typography.headlineMedium, color = black)
                Text("Choose which apps to track",
                    style = MaterialTheme.typography.bodyMedium, color = textSecondary)
                Spacer(modifier = Modifier.height(16.dp))

                val alreadyTracked = state.trackedApps.map { it.packageName }.toSet()
                val available = state.installedApps.filter { it.first !in alreadyTracked }

                LazyColumn(modifier = Modifier.heightIn(max = 400.dp)) {
                    items(available) { (packageName, appName) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AppIconCircle(appName)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(appName, modifier = Modifier.weight(1f),
                                fontSize = 15.sp, color = black)
                            TextButton(onClick = {
                                vm.addTrackedApp(packageName, appName)
                                showAddSheet = false
                            }) {
                                Text("Add", color = black)
                            }
                        }
                        HorizontalDivider(color = border, thickness = 0.5.dp)
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}