package com.example.screenshame.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.screenshame.ui.navigation.Screen
import com.example.screenshame.ui.theme.*

@Composable
fun BottomNav(navController: NavController, current: String) {
    NavigationBar(containerColor = white, tonalElevation = 0.dp) {
        NavigationBarItem(
            selected = current == "dashboard",
            onClick = {
                if (current != "dashboard")
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
            },
            icon = { Icon(Icons.Outlined.Home, contentDescription = "dash") },
            label = { Text("Dash") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = black,
                selectedTextColor = black,
                unselectedIconColor = textSecondary,
                unselectedTextColor = textSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = current == "limits",
            onClick = {
                if (current != "limits")
                    navController.navigate(Screen.Limits.route) {
                        popUpTo(Screen.Dashboard.route)
                    }
            },
            icon = { Icon(Icons.Outlined.Shield, contentDescription = "limits") },
            label = { Text("Limits") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = black,
                selectedTextColor = black,
                unselectedIconColor = textSecondary,
                unselectedTextColor = textSecondary,
                indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = current == "history",
            onClick = {
                if (current != "history")
                    navController.navigate(Screen.History.route) {
                        popUpTo(Screen.Dashboard.route)
                    }
            },
            icon = { Icon(Icons.Outlined.BarChart, contentDescription = "history") },
            label = { Text("History") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = black,
                selectedTextColor = black,
                unselectedIconColor = textSecondary,
                unselectedTextColor = textSecondary,
                indicatorColor = Color.Transparent
            )
        )
    }
}