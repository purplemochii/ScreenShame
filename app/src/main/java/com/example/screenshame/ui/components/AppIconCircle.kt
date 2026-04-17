package com.example.screenshame.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.screenshame.ui.theme.surface
import com.example.screenshame.ui.theme.textSecondary

@Composable
fun AppIconCircle(appName: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = appName.take(1).uppercase(),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = textSecondary
        )
    }
}