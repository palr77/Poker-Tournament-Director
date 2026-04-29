package com.poker.tournamentdirector.presentation.night

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.poker.tournamentdirector.presentation.main.AdMobBanner
import com.poker.tournamentdirector.presentation.main.DashboardPlaceholder

@Composable
fun NightScreen(
    viewModel: NightViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DashboardPlaceholder(
        title = "Night",
        status = "Not started",
        metricRows = listOf(
            "Players seated" to "0",
            "Night pot" to "0 MXN",
            "Bounty pool" to "0 MXN",
        ),
        icon = Icons.Filled.Groups,
        footer = {
            if (uiState.shouldShowAds && uiState.bannerAdUnitId.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                AdMobBanner(adUnitId = uiState.bannerAdUnitId)
            }
        },
    )
}
