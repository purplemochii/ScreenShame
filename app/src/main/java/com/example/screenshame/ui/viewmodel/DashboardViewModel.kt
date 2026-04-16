package com.example.screenshame.ui.viewmodel

import android.app.Application
import android.os.Message
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import com.example.screenshame.data.repository.AppUsageSummary
import com.example.screenshame.data.repository.UsageRepository
import com.example.screenshame.data.db.AppLimit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DashboardUiState (
    val isLoading: Boolean = true,
    val appUsage: List<AppUsageSummary> = emptyList(),
    val totalMinutesOverLimit: Int = 0,
    val roastMessage: String = ""
)

data class LimitsUiState (
    val isLoading: Boolean = true,
    val trackedApps: List<AppLimit> = emptyList(),
    val installedApps: List<Pair<String, String>> = emptyList()
)

data class HistoryUiState (
    val isLoading: Boolean = true,
    val weeklyData: List<Pair<String, Int>> = emptyList(),
    val totalWeeklyMinutes: Int = 0,
    val worstOffenses: List<AppUsageSummary> = emptyList()
)

class ScreenShameViewModel ( application: Application ) : AndroidViewModel ( application ) {
    private val repository = UsageRepository ( application )

    private val _dashboardState = MutableStateFlow(DashboardUiState() )
    val dashboardState: StateFlow<DashboardUiState> = _dashboardState

    private val _limitsState = MutableStateFlow(LimitsUiState() )
    val limitsState: StateFlow<LimitsUiState> = _limitsState

    private val _historyState = MutableStateFlow(HistoryUiState() )
    val historyState: StateFlow<HistoryUiState> = _historyState

    val roastMessage = listOf (
        "You said 30 minutes. It has been longer. You lied.",
        "Your ancestors built the pyramids. You scrolled reels.",
        "At this rate, your phone is your personality.",
        "Limit exceeded. Again. We're not surprised.",
        "You are the reason this app exists.",
        "This is not what 'just five more minutes' means.",
        "The app remembers everything. So does your screen time.",
        "Congratulations on your commitment to doing nothing.",
        "Your past self set that limit. Look what you did to them.",
        "Arguing with strangers online again.",
        "Your brain cells are melting.",
        "Basically a part-time job scrolling.",
        "The pyramids were built in less time than you spent on TikTok today."
    )

    fun loadDashboard () {
        viewModelScope.launch {
            _dashboardState.value = DashboardUiState ( isLoading = true )
            try {
                val usage = repository.getTodayUsage()
                val overLimitApps = usage.filter { it.isOverLimit }
                val totalOver = overLimitApps.sumOf {
                    it.usageMinutes - ( it.limitMinutes ?: 0 )
                }

                val roast = if ( overLimitApps.isNotEmpty() ) {
                    roastMessage.random()
                } else ""

                _dashboardState.value = DashboardUiState (
                    isLoading = false,
                    appUsage = usage,
                    totalMinutesOverLimit = totalOver,
                    roastMessage =roast
                )

                // save snapshots to room for history
                usage.forEach { repository.saveTodaySnapshot( it ) }
            } catch ( e: Exception ) {
                _dashboardState.value = DashboardUiState ( isLoading = false )
            }
        }
    }

    fun loadLimits() {
        viewModelScope.launch {
            _limitsState.value = LimitsUiState ( isLoading = true )
            try {
                val tracked = repository.getAllLimits()
                val installed = repository.getInstalledApps()
                _limitsState.value = LimitsUiState (
                    isLoading = false,
                    trackedApps = tracked,
                    installedApps = installed
                )
            } catch ( e: Exception ) {
                _limitsState.value = LimitsUiState ( isLoading = false )
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            _historyState.value = HistoryUiState ( isLoading = true )
            try {
                val weeklyData = repository.getWeeklyTotals()
                val total = weeklyData.sumOf { it.second }
                val worst = repository.getWorstOffenses()
                _historyState.value = HistoryUiState (
                    isLoading = false,
                    weeklyData = weeklyData,
                    totalWeeklyMinutes = total,
                    worstOffenses = worst
                )
            } catch ( e: Exception ) {
                _historyState.value = HistoryUiState ( isLoading = false )
            }
        }
    }

    fun addTrackedApp ( packageName: String, appName: String ) {
        viewModelScope.launch {
            repository.addTrackedApp ( packageName, appName )
            loadLimits()
        }
    }

    fun setLimit ( packageName: String, appName: String, minutes: Int ) {
        viewModelScope.launch {
            repository.setLimit( packageName, appName, minutes )
            loadLimits()
        }
    }

    fun removeTrackedApp ( packageName: String ) {
        viewModelScope.launch {
            repository.removeLimit( packageName )
            loadLimits()
        }
    }
}