package com.poker.tournamentdirector.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.poker.tournamentdirector.core.database.entity.BountyEventEntity
import com.poker.tournamentdirector.core.database.entity.ClockStateEntity
import com.poker.tournamentdirector.core.database.entity.ExportSnapshotEntity
import com.poker.tournamentdirector.core.database.entity.GuestParticipationEntity
import com.poker.tournamentdirector.core.database.entity.NightActionEventEntity
import com.poker.tournamentdirector.core.database.entity.NightEntity
import com.poker.tournamentdirector.core.database.entity.NightPlayerStateEntity
import com.poker.tournamentdirector.core.database.entity.NightResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NightDao {
    @Query("SELECT * FROM nights WHERE season_id = :seasonId ORDER BY night_number ASC")
    fun observeNightsForSeason(seasonId: Long): Flow<List<NightEntity>>

    @Query("SELECT * FROM nights WHERE id = :nightId")
    fun observeNight(nightId: Long): Flow<NightEntity?>

    @Upsert
    suspend fun upsertNight(night: NightEntity)

    @Query("SELECT * FROM night_player_states WHERE night_id = :nightId ORDER BY seat_number ASC")
    fun observePlayerStates(nightId: Long): Flow<List<NightPlayerStateEntity>>

    @Upsert
    suspend fun upsertPlayerState(state: NightPlayerStateEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAction(event: NightActionEventEntity)

    @Query("SELECT * FROM night_action_events WHERE night_id = :nightId ORDER BY created_at ASC")
    fun observeActions(nightId: Long): Flow<List<NightActionEventEntity>>

    @Upsert
    suspend fun upsertClockState(state: ClockStateEntity)

    @Query("SELECT * FROM clock_states WHERE night_id = :nightId")
    fun observeClockState(nightId: Long): Flow<ClockStateEntity?>

    @Upsert
    suspend fun upsertNightResults(results: List<NightResultEntity>)

    @Query("SELECT * FROM night_results WHERE night_id = :nightId ORDER BY place ASC")
    fun observeNightResults(nightId: Long): Flow<List<NightResultEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBountyEvent(event: BountyEventEntity)

    @Query("SELECT * FROM bounty_events WHERE night_id = :nightId ORDER BY created_at ASC")
    fun observeBountyEvents(nightId: Long): Flow<List<BountyEventEntity>>

    @Upsert
    suspend fun upsertGuestParticipation(participation: GuestParticipationEntity)

    @Query("SELECT * FROM guest_participations WHERE night_id = :nightId")
    fun observeGuestParticipations(nightId: Long): Flow<List<GuestParticipationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExportSnapshot(snapshot: ExportSnapshotEntity)

    @Query("SELECT * FROM export_snapshots WHERE night_id = :nightId ORDER BY created_at DESC")
    fun observeExportSnapshots(nightId: Long): Flow<List<ExportSnapshotEntity>>
}
