package com.poker.tournamentdirector.domain.repository

import com.poker.tournamentdirector.domain.model.NightSummary
import kotlinx.coroutines.flow.Flow

interface NightRepository {
    fun observeNightsForSeason(seasonId: Long): Flow<List<NightSummary>>
}
