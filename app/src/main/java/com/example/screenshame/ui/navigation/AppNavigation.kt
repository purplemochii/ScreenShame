package com.example.screenshame.ui.navigation

import android.app.AppOpsManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.screenshame.ui.screens.*
import com.example.screenshame.ui.viewmodel.ScreenShameViewModel
import com.example.screenshame.util.AuthManager

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Permission : Screen("permission")
    object Dashboard : Screen("dashboard")
    object Limits : Screen("limits")
    object History : Screen("history")
}

fun hasUsagePermission ( context: Context ) : Boolean {
    val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
    val mode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
        appOps.unsafeCheckOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
    } else {
        @Suppress("DEPRECATION")
        appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            context.packageName
        )
    }
    return mode == AppOpsManager.MODE_ALLOWED
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authManager = AuthManager(context)

    // decide the starting destination based on permissions
    //val start = Screen.Permission.route
    val start = when {
        !hasUsagePermission(context) -> Screen.Permission.route
        !authManager.isLoggedIn() -> Screen.Login.route
        else -> Screen.Dashboard.route
    }
    val vm: ScreenShameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = start
    ) {
        composable ( Screen.Permission.route ) {
            PermissionScreen ( onPermissionGranted = {
                navController.navigate(Screen.Login.route ) {
                    popUpTo ( Screen.Permission.route ) { inclusive = true }
                }
            })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                authManager = authManager,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController, vm = vm)
        }
        composable(Screen.Limits.route) {
            LimitsScreen(navController = navController, vm = vm)
        }
        composable(Screen.History.route) {
            HistoryScreen(navController = navController, vm = vm)
        }
    }
}