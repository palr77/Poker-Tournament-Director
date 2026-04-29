package com.poker.tournamentdirector.presentation.stats

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.runtime.Composable
import com.poker.tournamentdirector.presentation.main.DashboardPlaceholder

@Composable
fun StatsScreen() {
    DashboardPlaceholder(
        title = "Stats",
        status = "Season totals",
        metricRows = listOf(
            "Leader" to "-",
            "Fish" to "-",
            "Sniper" to "-",
        ),
        icon = Icons.Filled.BarChart,
    )
}
