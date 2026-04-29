package com.poker.tournamentdirector.domain.repository

import com.poker.tournamentdirector.domain.model.SeasonSummary
import kotlinx.coroutines.flow.Flow

interface SeasonRepository {
    fun observeSeasons(): Flow<List<SeasonSummary>>

    fun observeActiveSeason(): Flow<SeasonSummary?>
}
