package com.example.screenshame

import android.app.Application
import com.example.screenshame.util.ShameNotificationWorker

class ScreenShameApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ShameNotificationWorker.schedule(this)
    }
}