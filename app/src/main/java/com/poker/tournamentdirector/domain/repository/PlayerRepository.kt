package com.poker.tournamentdirector.domain.repository

import com.poker.tournamentdirector.domain.model.PlayerSummary
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun observeActivePlayers(): Flow<List<PlayerSummary>>
}
