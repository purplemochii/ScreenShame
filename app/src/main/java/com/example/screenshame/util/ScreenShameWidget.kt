package com.example.screenshame.util

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.text.*
import androidx.glance.unit.ColorProvider
import com.example.screenshame.MainActivity
import com.example.screenshame.data.repository.UsageRepository
import androidx.glance.action.clickable


class ScreenShameWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = UsageRepository(context)
        val usage = try {
            repository.getTodayUsage().filter {
                it.limitMinutes != null && it.limitMinutes > 0
            }
        } catch (e: Exception) {
            emptyList()
        }

        val topOffender = usage.maxByOrNull { it.usageMinutes }

        provideContent {
            WidgetContent(topOffender = topOffender?.let {
                Triple(it.appName, it.usageMinutes, it.isOverLimit)
            })
        }
    }
}
@SuppressLint( "RestrictedApi" )
@Composable
fun WidgetContent(topOffender: Triple<String, Int, Boolean>?) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(ColorProvider(Color(0xFFFDF8F2)))
            .padding(22.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Column(modifier = GlanceModifier.fillMaxSize()) {
            // header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ScreenShame",
                    style = TextStyle(
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(Color(0xFF0A0A0A))
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(10.dp))

            if (topOffender == null) {
                Text(
                    text = "No limits set yet.",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = ColorProvider(Color(0xFF6B6B6B))
                    )
                )
            } else {
                val (appName, minutes, isOver) = topOffender
                val hours = minutes / 60
                val mins = minutes % 60
                val timeStr = if (hours > 0) "${hours}h ${mins}m" else "${mins}m"
                val statusColor = if (isOver) Color(0xFFE5352A) else Color(0xFF0A0A0A)

                Text(
                    text = "top offender today",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = ColorProvider(Color(0xFF6B6B6B))
                    )
                )
                Spacer(modifier = GlanceModifier.height(6.dp))
                Text(
                    text = appName,
                    style = TextStyle(
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorProvider(Color(0xFF0A0A0A))
                    )
                )
                Spacer(modifier = GlanceModifier.height(4.dp))
                Text(
                    text = timeStr + if (isOver) " · over limit" else " · within limit",
                    style = TextStyle(
                        fontSize = 18.sp,
                        color = ColorProvider(statusColor)
                    )
                )
            }
        }
    }
}

class ScreenShameWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ScreenShameWidget()
}