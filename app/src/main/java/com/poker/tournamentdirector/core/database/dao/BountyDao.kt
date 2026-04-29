package com.poker.tournamentdirector.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.poker.tournamentdirector.core.database.entity.BountyEventEntity
import com.poker.tournamentdirector.core.database.entity.BountyLedgerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BountyDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBountyEvent(event: BountyEventEntity): Long

    @Query("SELECT * FROM bounty_events WHERE night_id = :nightId ORDER BY created_at ASC, id ASC")
    fun observeBountyEvents(nightId: Long): Flow<List<BountyEventEntity>>

    @Query("SELECT * FROM bounty_events WHERE night_id = :nightId ORDER BY created_at ASC, id ASC")
    suspend fun getBountyEvents(nightId: Long): List<BountyEventEntity>

    @Query(
        """
        SELECT * FROM bounty_events
        WHERE night_id = :nightId AND bounty_player_id = :bountyPlayerId
        LIMIT 1
        """,
    )
    suspend fun getBountyEventForPlayer(
        nightId: Long,
        bountyPlayerId: Long,
    ): BountyEventEntity?

    @Query(
        """
        SELECT COALESCE(SUM(amount_cents), 0)
        FROM bounty_events
        WHERE night_id = :nightId AND collected_by_player_id = :playerId
        """,
    )
    suspend fun getBountyPayoutForPlayer(
        nightId: Long,
        playerId: Long,
    ): Long

    @Upsert
    suspend fun upsertLedger(ledger: BountyLedgerEntity)

    @Upsert
    suspend fun upsertLedgers(ledgers: List<BountyLedgerEntity>)

    @Query("DELETE FROM bounty_ledgers WHERE season_id = :seasonId")
    suspend fun deleteLedgers(seasonId: Long)

    @Query(
        """
        SELECT * FROM bounty_ledgers
        WHERE season_id = :seasonId AND player_id = :playerId
        LIMIT 1
        """,
    )
    suspend fun getLedger(
        seasonId: Long,
        playerId: Long,
    ): BountyLedgerEntity?

    @Query("SELECT * FROM bounty_ledgers WHERE season_id = :seasonId ORDER BY amount_won_cents DESC")
    fun observeLedgers(seasonId: Long): Flow<List<BountyLedgerEntity>>

    @Query("SELECT * FROM bounty_ledgers WHERE season_id = :seasonId ORDER BY amount_won_cents DESC")
    suspend fun getLedgers(seasonId: Long): List<BountyLedgerEntity>
}
