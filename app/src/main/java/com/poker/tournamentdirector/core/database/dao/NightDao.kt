package com.poker.tournamentdirector.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.poker.tournamentdirector.core.database.entity.ClockStateEntity
import com.poker.tournamentdirector.core.database.entity.GuestParticipationEntity
import com.poker.tournamentdirector.core.database.entity.NightActionEventEntity
import com.poker.tournamentdirector.core.database.entity.NightEntity
import com.poker.tournamentdirector.core.database.entity.NightPlayerStateEntity
import com.poker.tournamentdirector.core.database.entity.NightResultEntity
import java.time.Instant
import kotlinx.coroutines.flow.Flow

@Dao
interface NightDao {
    @Query("SELECT * FROM nights WHERE season_id = :seasonId ORDER BY night_number ASC")
    fun observeNightsForSeason(seasonId: Long): Flow<List<NightEntity>>

    @Query("SELECT * FROM nights WHERE season_id = :seasonId ORDER BY night_number ASC")
    suspend fun getNightsForSeason(seasonId: Long): List<NightEntity>

    @Query(
        """
        SELECT * FROM nights
        WHERE season_id = :seasonId AND status = 'Closed'
        ORDER BY night_number ASC
        """,
    )
    suspend fun getClosedNightsForSeason(seasonId: Long): List<NightEntity>

    @Query("SELECT * FROM nights WHERE id = :nightId")
    fun observeNight(nightId: Long): Flow<NightEntity?>

    @Query("SELECT * FROM nights WHERE id = :nightId")
    suspend fun getNight(nightId: Long): NightEntity?

    @Query(
        """
        SELECT * FROM nights
        WHERE season_id = :seasonId AND season_date_id = :seasonDateId
        LIMIT 1
        """,
    )
    suspend fun getNightForSeasonDate(
        seasonId: Long,
        seasonDateId: Long,
    ): NightEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertNight(night: NightEntity): Long

    @Update
    suspend fun updateNight(night: NightEntity)

    @Upsert
    suspend fun upsertNight(night: NightEntity)

    @Query(
        """
        UPDATE nights
        SET status = 'Active', started_at = :startedAt
        WHERE id = :nightId
        """,
    )
    suspend fun markNightStarted(
        nightId: Long,
        startedAt: Instant,
    )

    @Query(
        """
        UPDATE nights
        SET status = 'Closed', closed_at = :closedAt
        WHERE id = :nightId
        """,
    )
    suspend fun markNightClosed(
        nightId: Long,
        closedAt: Instant,
    )

    @Query(
        """
        SELECT * FROM night_player_states
        WHERE night_id = :nightId
        ORDER BY
            CASE WHEN seat_number IS NULL THEN 1 ELSE 0 END,
            seat_number ASC,
            player_id ASC
        """,
    )
    fun observePlayerStates(nightId: Long): Flow<List<NightPlayerStateEntity>>

    @Query(
        """
        SELECT * FROM night_player_states
        WHERE night_id = :nightId
        ORDER BY
            CASE WHEN seat_number IS NULL THEN 1 ELSE 0 END,
            seat_number ASC,
            player_id ASC
        """,
    )
    suspend fun getPlayerStates(nightId: Long): List<NightPlayerStateEntity>

    @Query(
        """
        SELECT * FROM night_player_states
        WHERE night_id = :nightId AND player_id = :playerId
        LIMIT 1
        """,
    )
    suspend fun getPlayerState(
        nightId: Long,
        playerId: Long,
    ): NightPlayerStateEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlayerState(state: NightPlayerStateEntity): Long

    @Update
    suspend fun updatePlayerState(state: NightPlayerStateEntity)

    @Upsert
    suspend fun upsertPlayerState(state: NightPlayerStateEntity)

    @Upsert
    suspend fun upsertPlayerStates(states: List<NightPlayerStateEntity>)

    @Query("SELECT COUNT(*) FROM night_player_states WHERE night_id = :nightId AND status = 'Active'")
    fun observePlayersLeft(nightId: Long): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAction(event: NightActionEventEntity): Long

    @Query("SELECT * FROM night_action_events WHERE night_id = :nightId ORDER BY created_at ASC, id ASC")
    fun observeActions(nightId: Long): Flow<List<NightActionEventEntity>>

    @Query("SELECT * FROM night_action_events WHERE night_id = :nightId ORDER BY created_at ASC, id ASC")
    suspend fun getActions(nightId: Long): List<NightActionEventEntity>

    @Query(
        """
        SELECT * FROM night_action_events
        WHERE night_id IN (:nightIds)
        ORDER BY created_at ASC, id ASC
        """,
    )
    suspend fun getActionsForNights(nightIds: List<Long>): List<NightActionEventEntity>

    @Query(
        """
        SELECT * FROM night_action_events
        WHERE night_id = :nightId AND player_id = :playerId
        ORDER BY created_at ASC, id ASC
        """,
    )
    suspend fun getActionsForPlayer(
        nightId: Long,
        playerId: Long,
    ): List<NightActionEventEntity>

    @Query(
        """
        SELECT COALESCE(SUM(amount_cents), 0)
        FROM night_action_events
        WHERE night_id = :nightId
            AND is_bounty_segment = 0
            AND action IN ('BuyIn', 'Rebuy', 'AddOn')
        """,
    )
    fun observeNightPotCents(nightId: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(amount_cents), 0)
        FROM night_action_events
        WHERE night_id = :nightId
            AND is_bounty_segment = 0
            AND action IN ('BuyIn', 'Rebuy', 'AddOn')
        """,
    )
    suspend fun getNightPotCents(nightId: Long): Long

    @Query(
        """
        SELECT COALESCE(SUM(amount_cents), 0)
        FROM night_action_events
        WHERE night_id = :nightId
            AND is_bounty_segment = 1
            AND action IN ('BuyIn', 'Rebuy', 'AddOn')
        """,
    )
    fun observeBountySegmentCents(nightId: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM(amount_cents), 0)
        FROM night_action_events
        WHERE night_id = :nightId
            AND is_bounty_segment = 1
            AND action IN ('BuyIn', 'Rebuy', 'AddOn')
        """,
    )
    suspend fun getBountySegmentCents(nightId: Long): Long

    @Query(
        """
        SELECT COALESCE(SUM(amount_cents), 0)
        FROM night_action_events
        WHERE night_id = :nightId
            AND player_id = :playerId
            AND is_bounty_segment = 1
            AND action IN ('BuyIn', 'Rebuy', 'AddOn')
        """,
    )
    suspend fun getBountySegmentForPlayerCents(
        nightId: Long,
        playerId: Long,
    ): Long

    @Upsert
    suspend fun upsertClockState(state: ClockStateEntity)

    @Query("SELECT * FROM clock_states WHERE night_id = :nightId")
    fun observeClockState(nightId: Long): Flow<ClockStateEntity?>

    @Query("SELECT * FROM clock_states WHERE night_id = :nightId")
    suspend fun getClockState(nightId: Long): ClockStateEntity?

    @Query("DELETE FROM night_results WHERE night_id = :nightId")
    suspend fun deleteNightResults(nightId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNightResults(results: List<NightResultEntity>)

    @Upsert
    suspend fun upsertNightResult(result: NightResultEntity)

    @Upsert
    suspend fun upsertNightResults(results: List<NightResultEntity>)

    @Query("SELECT * FROM night_results WHERE night_id = :nightId ORDER BY place ASC")
    fun observeNightResults(nightId: Long): Flow<List<NightResultEntity>>

    @Query("SELECT * FROM night_results WHERE night_id = :nightId ORDER BY place ASC")
    suspend fun getNightResults(nightId: Long): List<NightResultEntity>

    @Query(
        """
        SELECT * FROM night_results
        WHERE night_id IN (:nightIds)
        ORDER BY night_id ASC, place ASC
        """,
    )
    suspend fun getResultsForNights(nightIds: List<Long>): List<NightResultEntity>

    @Query(
        """
        SELECT COALESCE(SUM(points), 0)
        FROM night_results
        WHERE player_id = :playerId
        """,
    )
    suspend fun getLifetimePoints(playerId: Long): Int

    @Query(
        """
        SELECT * FROM night_results
        WHERE player_id = :playerId
        ORDER BY night_id ASC, place ASC
        """,
    )
    suspend fun getResultHistoryForPlayer(playerId: Long): List<NightResultEntity>

    @Upsert
    suspend fun upsertGuestParticipation(participation: GuestParticipationEntity)

    @Query("SELECT * FROM guest_participations WHERE night_id = :nightId ORDER BY created_at ASC")
    fun observeGuestParticipations(nightId: Long): Flow<List<GuestParticipationEntity>>

    @Query("SELECT * FROM guest_participations WHERE night_id = :nightId ORDER BY created_at ASC")
    suspend fun getGuestParticipations(nightId: Long): List<GuestParticipationEntity>

    @Query(
        """
        SELECT * FROM guest_participations
        WHERE night_id = :nightId AND guest_player_id = :guestPlayerId
        LIMIT 1
        """,
    )
    suspend fun getGuestParticipation(
        nightId: Long,
        guestPlayerId: Long,
    ): GuestParticipationEntity?
}
