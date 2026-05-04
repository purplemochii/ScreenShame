package com.example.screenshame.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.screenshame.ui.theme.*

@Composable
fun PermissionScreen ( onPermissionGranted: () -> Unit) {
    val context = LocalContext.current

    Box (
        modifier = Modifier
            .fillMaxSize()
            .padding( horizontal = 24.dp )
    ) {
        Column(
            modifier = Modifier.padding(top = 64.dp)
        ) {
            // icno
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, color = border)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = red,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // headline
            Text(
                text = "The phone does the snitching.",
                style = MaterialTheme.typography.headlineLarge,
                color = black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // subtext
            Text(
                text = "To read your actual screen time, ScreenShame requires the UsageStatsManager API. No self-reporting, no lying.",
                style = MaterialTheme.typography.bodyMedium,
                color = textSecondary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // permissions card I
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, border)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null,
                        tint = black,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "PACKAGE_USAGE_STATS",
                            style = MaterialTheme.typography.bodyMedium,
                            color = black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Reads the exact minutes spent in each app directly from the Android OS.",
                            style = MaterialTheme.typography.bodySmall,
                            color = textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // permissions card II
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, border)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = black,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "Local & Encrypted",
                            style = MaterialTheme.typography.bodyMedium,
                            color = black
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Your shame is private. Data is stored locally in an encrypted Room database.",
                            style = MaterialTheme.typography.bodySmall,
                            color = textSecondary,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }

        // bottom section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding( bottom = 48.dp )
        ) {
            // lil redirection notice
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape( 12.dp ),
                color = surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, border)
            ) {
                Row(
                    modifier = Modifier.padding( 12.dp ),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon (
                        imageVector = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint = black,
                        modifier = Modifier.size( 16.dp )
                    )
                    Spacer( modifier = Modifier.width( 12.dp ) )

                    Text (
                        text = "You will be redirected to Android Settings.",
                        color = black,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer ( modifier = Modifier.height( 12.dp ) )

            // grant button
            Button(
                onClick = {
                    // open the usage access settings page
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS )
                    context.startActivity(intent)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = black,
                    contentColor = white
                ),
                shape = RoundedCornerShape(12.dp ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text (
                    text = "Grant Usage Access",
                    fontSize = 18.sp
                )
            }
        }
    }
}

