package com.example.screenshame

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.screenshame.ui.theme.ScreenShameTheme
import com.example.screenshame.ui.navigation.AppNavigation

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // request notifs
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ) {
            if (ContextCompat.checkSelfPermission( this, Manifest.permission.POST_NOTIFICATIONS ) != PackageManager.PERMISSION_GRANTED ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS )
            }
        }

        setContent {
            ScreenShameTheme {
                AppNavigation()
            }
        }
    }
}