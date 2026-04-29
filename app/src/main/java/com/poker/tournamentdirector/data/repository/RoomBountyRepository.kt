package com.poker.tournamentdirector.data.repository

import androidx.room.withTransaction
import com.poker.tournamentdirector.core.database.TournamentDirectorDatabase
import com.poker.tournamentdirector.core.database.dao.BountyDao
import com.poker.tournamentdirector.core.database.dao.NightDao
import com.poker.tournamentdirector.core.database.entity.BountyLedgerEntity
import com.poker.tournamentdirector.data.mapper.toDomain
import com.poker.tournamentdirector.domain.model.BountyEvent
import com.poker.tournamentdirector.domain.model.BountyLedger
import com.poker.tournamentdirector.domain.repository.BountyRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomBountyRepository @Inject constructor(
    private val database: TournamentDirectorDatabase,
    private val bountyDao: BountyDao,
    private val nightDao: NightDao,
) : BountyRepository {
    override fun observeBountyEvents(nightId: Long): Flow<List<BountyEvent>> =
        bountyDao.observeBountyEvents(nightId).map { events -> events.map { it.toDomain() } }

    override suspend fun getBountyEvents(nightId: Long): List<BountyEvent> =
        bountyDao.getBountyEvents(nightId).map { it.toDomain() }

    override fun observeBountyLedger(seasonId: Long): Flow<List<BountyLedger>> =
        bountyDao.observeLedgers(seasonId).map { ledgers -> ledgers.map { it.toDomain() } }

    override suspend fun getBountyLedger(seasonId: Long): List<BountyLedger> =
        bountyDao.getLedgers(seasonId).map { it.toDomain() }

    override suspend fun getBountyLedgerForPlayer(
        seasonId: Long,
        playerId: Long,
    ): BountyLedger? =
        bountyDao.getLedger(seasonId, playerId)?.toDomain()

    override suspend fun rebuildBountyLedger(seasonId: Long) {
        database.withTransaction {
            val nights = nightDao.getNightsForSeason(seasonId)
            val events = nights.flatMap { night -> bountyDao.getBountyEvents(night.id) }
            val ledgers = mutableMapOf<Long, BountyLedgerEntity>()

            events.forEach { event ->
                val winner = ledgers[event.collectedByPlayerId]
                    ?: BountyLedgerEntity(seasonId = seasonId, playerId = event.collectedByPlayerId)
                val bountyPlayer = ledgers[event.bountyPlayerId]
                    ?: BountyLedgerEntity(seasonId = seasonId, playerId = event.bountyPlayerId)

                ledgers[event.collectedByPlayerId] = winner.copy(
                    bountiesWon = winner.bountiesWon + 1,
                    amountWonCents = winner.amountWonCents + event.amountCents,
                )
                ledgers[event.bountyPlayerId] = bountyPlayer.copy(
                    bountiesLost = bountyPlayer.bountiesLost + 1,
                    amountLostCents = bountyPlayer.amountLostCents + event.amountCents,
                )
            }

            bountyDao.deleteLedgers(seasonId)
            bountyDao.upsertLedgers(ledgers.values.toList())
        }
    }
}
