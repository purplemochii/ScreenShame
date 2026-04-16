package com.example.screenshame.data.repository

import android.app.usage.UsageStatsManager
import android.content.Context
import androidx.compose.animation.core.snap
import androidx.compose.ui.graphics.Path
import com.example.screenshame.data.db.AppDatabase
import com.example.screenshame.data.db.AppLimit
import com.example.screenshame.data.db.UsageSnapshot
import java.text.SimpleDateFormat
import java.util.*

// dashboard display (per-app)
data class AppUsageSummary (
    val packageName : String,
    val appName: String,
    val usageMinutes : Int,
    val limitMinutes : Int?, //some apps might not have a limt set
    val isOverLimit : Boolean
)

class UsageRepository ( private val context: Context ) {
    private val db = AppDatabase.getInstance( context )
    private val limitDao = db.appLimitDao()
    private val snapshotDao = db.usageSnapshotDao()

    // pull todays usage from the os directly
    suspend fun getTodayUsage() : List<AppUsageSummary> {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE)
            as UsageStatsManager

        // get start and end of day
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0 )
            set(Calendar.MINUTE, 0 )
            set(Calendar.SECOND, 0 )
            set(Calendar.MILLISECOND, 0 )
        }

        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()

        // querying the os for usage stats
        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )

        // get the limits from roomdb
        val limits = limitDao.getAll().associateBy { it.packageName }

        // get package manager to give app names
        val packageManager = context.packageManager

        return usageStats
            .filter { it.totalTimeInForeground > 0 }
            .mapNotNull { stat ->
                // sjip system and unknown apps
                val appName = try {
                   packageManager.getApplicationLabel(
                       packageManager.getApplicationInfo( stat.packageName, 0 )
                   ).toString()
                } catch ( e: Exception ) {
                    return@mapNotNull null
                }

                val usageMinutes = ( stat.totalTimeInForeground / 1000 / 60 ).toInt()
                val limit = limits[stat.packageName]

                AppUsageSummary (
                    packageName = stat.packageName,
                    appName = appName,
                    usageMinutes = usageMinutes,
                    limitMinutes = limit?.dailyLimitMinutes,
                    isOverLimit = limit != null && usageMinutes > limit.dailyLimitMinutes
                )
            }
            .sortedByDescending { it.usageMinutes } // most used app first
    }

    // save or update a limit for an app
    suspend fun setLimit( packageName: String, appName: String, limitMinutes: Int ) {
        limitDao.upsert(AppLimit( packageName, appName, limitMinutes ) )
    }

    // remove a limit
    suspend fun removeLimit( packageName: String ) {
        val limit = limitDao.getLimit( packageName ) ?: return
        limitDao.delete( limit )
    }

    // get all limits (thsi is for the limits screen)
    suspend fun getAllLimits() : List<AppLimit> {
        return limitDao.getAll()
    }

    // save today's stats to roomdb (called periodically)
    suspend fun saveTodaySnapshot ( summary : AppUsageSummary ) {
        val today = SimpleDateFormat ( "dd-MM-yyyy", Locale.getDefault() ).format( Date() )
        snapshotDao.upsert (
            UsageSnapshot (
                packageName = summary.packageName,
                appName = summary.appName,
                date = today,
                usageMinutes = summary.usageMinutes
            )
        )
    }

    // get 7-day history for each app (this is for history screen)
    suspend fun getLast7Days ( packageName: String ) : List<UsageSnapshot> {
        return snapshotDao.getlast7days( packageName )
    }

    fun getInstalledApps(): List<Pair<String, String>> {
        val packageManager = context.packageManager
        val intent = android.content.Intent(android.content.Intent.ACTION_MAIN, null)
        intent.addCategory(android.content.Intent.CATEGORY_LAUNCHER)
        return packageManager.queryIntentServices(intent, 0)
            .mapNotNull { info ->
                val packageName = info.activityInfo.packageName
                val appName = info.loadLabel( packageManager ).toString()
                if ( packageName == context.packageName ) null
                else Pair ( packageName, appName )
            }
            .sortedBy { it.second }
    }

    // add apps to tracking list but no limt sey yet
    suspend fun addTrackedApp ( packageName: String, appName: String ) {
        limitDao.upsert( AppLimit( packageName, appName, 0 ) )
    }

    // get 7 day totals across all apps for the history chart
    suspend fun getWeeklyTotals(): List<Pair<String, Int>> {
        val calendar = Calendar.getInstance()
        val result = mutableListOf<Pair<String, Int>>()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dayLabel = SimpleDateFormat("EEE", Locale.getDefault())

        for ( i in 6 downTo 0 ) {
            calendar.time = Date()
            calendar.add(Calendar.DAY_OF_YEAR, -i )
            val dateStr = sdf.format( calendar.time )
            val label = dayLabel.format( calendar.time )
            val snapshots = snapshotDao.getSnapshotsForDate(dateStr)
            val total = snapshots.sumOf { it.usageMinutes }
            result.add(Pair(label, total))
        }
        return result
    }

    // get worst offenses (apps that go over limit across all snapshots)
    suspend fun getWorstOffenses(): List<AppUsageSummary> {
        val limits = limitDao.getAll().associateBy { it.packageName }
        val today = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(Date())
        val snapshots = snapshotDao.getSnapshotsForDate(today)

        return snapshots.mapNotNull { snap ->
            val limit = limits[snap.packageName] ?: return@mapNotNull null
            if ( snap.usageMinutes <= limit.dailyLimitMinutes ) return@mapNotNull null

            AppUsageSummary (
                packageName = snap.packageName,
                appName = snap.appName,
                usageMinutes = snap.usageMinutes,
                limitMinutes = limit.dailyLimitMinutes,
                isOverLimit = true
            )
        }.sortedByDescending { it.usageMinutes - ( it.limitMinutes ?: 0 ) }
    }
}