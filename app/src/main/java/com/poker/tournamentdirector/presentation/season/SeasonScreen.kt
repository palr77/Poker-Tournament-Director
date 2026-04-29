package com.poker.tournamentdirector.presentation.season

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.runtime.Composable
import com.poker.tournamentdirector.presentation.main.DashboardPlaceholder

@Composable
fun SeasonScreen() {
    DashboardPlaceholder(
        title = "Season",
        status = "No active season",
        metricRows = listOf(
            "Registered players" to "0",
            "Season pool" to "0 MXN",
            "Dates scheduled" to "0",
        ),
        icon = Icons.Filled.Event,
    )
}
