package com.poker.tournamentdirector.domain.repository

import com.poker.tournamentdirector.domain.model.BountyEvent
import com.poker.tournamentdirector.domain.model.BountyLedger
import kotlinx.coroutines.flow.Flow

interface BountyRepository {
    fun observeBountyEvents(nightId: Long): Flow<List<BountyEvent>>

    suspend fun getBountyEvents(nightId: Long): List<BountyEvent>

    fun observeBountyLedger(seasonId: Long): Flow<List<BountyLedger>>

    suspend fun getBountyLedger(seasonId: Long): List<BountyLedger>

    suspend fun getBountyLedgerForPlayer(
        seasonId: Long,
        playerId: Long,
    ): BountyLedger?

    suspend fun rebuildBountyLedger(seasonId: Long)
}
