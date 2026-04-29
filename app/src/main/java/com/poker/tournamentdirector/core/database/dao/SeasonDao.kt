package com.poker.tournamentdirector.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.poker.tournamentdirector.core.database.entity.BlindLevelEntity
import com.poker.tournamentdirector.core.database.entity.PlayerEntity
import com.poker.tournamentdirector.core.database.entity.SeasonDateEntity
import com.poker.tournamentdirector.core.database.entity.SeasonEntity
import com.poker.tournamentdirector.core.database.entity.SeasonPlayerRegistrationEntity
import com.poker.tournamentdirector.core.database.entity.SeasonStandingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SeasonDao {
    @Query("SELECT * FROM seasons ORDER BY created_at DESC")
    fun observeSeasons(): Flow<List<SeasonEntity>>

    @Query("SELECT * FROM seasons WHERE is_active = 1 ORDER BY created_at DESC LIMIT 1")
    fun observeActiveSeason(): Flow<SeasonEntity?>

    @Query("SELECT * FROM seasons WHERE id = :seasonId")
    fun observeSeason(seasonId: Long): Flow<SeasonEntity?>

    @Query("SELECT * FROM seasons WHERE id = :seasonId")
    suspend fun getSeason(seasonId: Long): SeasonEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSeason(season: SeasonEntity): Long

    @Update
    suspend fun updateSeason(season: SeasonEntity)

    @Upsert
    suspend fun upsertSeason(season: SeasonEntity)

    @Query("UPDATE seasons SET is_active = 0 WHERE id != :seasonId")
    suspend fun deactivateOtherSeasons(seasonId: Long)

    @Query("UPDATE seasons SET is_active = CASE WHEN id = :seasonId THEN 1 ELSE 0 END")
    suspend fun setActiveSeason(seasonId: Long)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSeasonDate(date: SeasonDateEntity): Long

    @Upsert
    suspend fun upsertSeasonDate(date: SeasonDateEntity)

    @Upsert
    suspend fun upsertSeasonDates(dates: List<SeasonDateEntity>)

    @Delete
    suspend fun deleteSeasonDate(date: SeasonDateEntity)

    @Query("DELETE FROM season_dates WHERE season_id = :seasonId")
    suspend fun deleteSeasonDates(seasonId: Long)

    @Query("SELECT * FROM season_dates WHERE id = :seasonDateId")
    suspend fun getSeasonDate(seasonDateId: Long): SeasonDateEntity?

    @Query("SELECT * FROM season_dates WHERE season_id = :seasonId ORDER BY display_order ASC")
    fun observeSeasonDates(seasonId: Long): Flow<List<SeasonDateEntity>>

    @Query("SELECT * FROM season_dates WHERE season_id = :seasonId ORDER BY display_order ASC")
    suspend fun getSeasonDates(seasonId: Long): List<SeasonDateEntity>

    @Query("SELECT COUNT(*) FROM season_dates WHERE season_id = :seasonId")
    suspend fun countSeasonDates(seasonId: Long): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRegistration(registration: SeasonPlayerRegistrationEntity): Long

    @Update
    suspend fun updateRegistration(registration: SeasonPlayerRegistrationEntity)

    @Upsert
    suspend fun upsertRegistration(registration: SeasonPlayerRegistrationEntity)

    @Delete
    suspend fun deleteRegistration(registration: SeasonPlayerRegistrationEntity)

    @Query(
        """
        SELECT * FROM season_player_registrations
        WHERE season_id = :seasonId AND player_id = :playerId
        """,
    )
    suspend fun getRegistration(
        seasonId: Long,
        playerId: Long,
    ): SeasonPlayerRegistrationEntity?

    @Query("SELECT * FROM season_player_registrations WHERE season_id = :seasonId")
    fun observeRegistrations(seasonId: Long): Flow<List<SeasonPlayerRegistrationEntity>>

    @Query("SELECT * FROM season_player_registrations WHERE season_id = :seasonId")
    suspend fun getRegistrations(seasonId: Long): List<SeasonPlayerRegistrationEntity>

    @Query(
        """
        SELECT players.* FROM players
        INNER JOIN season_player_registrations registrations
            ON registrations.player_id = players.id
        WHERE registrations.season_id = :seasonId
        ORDER BY registrations.registration_number ASC, players.display_name COLLATE NOCASE ASC
        """,
    )
    fun observeSeasonRoster(seasonId: Long): Flow<List<PlayerEntity>>

    @Query(
        """
        SELECT players.* FROM players
        INNER JOIN season_player_registrations registrations
            ON registrations.player_id = players.id
        WHERE registrations.season_id = :seasonId
        ORDER BY registrations.registration_number ASC, players.display_name COLLATE NOCASE ASC
        """,
    )
    suspend fun getSeasonRoster(seasonId: Long): List<PlayerEntity>

    @Query(
        """
        SELECT COALESCE(MAX(registration_number), 0) + 1
        FROM season_player_registrations
        WHERE season_id = :seasonId
        """,
    )
    suspend fun nextRegistrationNumber(seasonId: Long): Int

    @Query(
        """
        SELECT COALESCE(SUM(inscription_paid_cents), 0)
        FROM season_player_registrations
        WHERE season_id = :seasonId AND is_payout_eligible = 1
        """,
    )
    suspend fun getSeasonPoolCents(seasonId: Long): Long

    @Upsert
    suspend fun upsertBlindLevel(level: BlindLevelEntity)

    @Upsert
    suspend fun upsertBlindLevels(levels: List<BlindLevelEntity>)

    @Query("DELETE FROM blind_levels WHERE season_id = :seasonId")
    suspend fun deleteBlindLevels(seasonId: Long)

    @Query("SELECT * FROM blind_levels WHERE season_id = :seasonId ORDER BY level_number ASC")
    fun observeBlindLevels(seasonId: Long): Flow<List<BlindLevelEntity>>

    @Query("SELECT * FROM blind_levels WHERE season_id = :seasonId ORDER BY level_number ASC")
    suspend fun getBlindLevels(seasonId: Long): List<BlindLevelEntity>

    @Query(
        """
        SELECT * FROM blind_levels
        WHERE season_id = :seasonId AND level_number = :levelNumber
        """,
    )
    suspend fun getBlindLevel(
        seasonId: Long,
        levelNumber: Int,
    ): BlindLevelEntity?

    @Upsert
    suspend fun upsertStanding(standing: SeasonStandingEntity)

    @Upsert
    suspend fun upsertStandings(standings: List<SeasonStandingEntity>)

    @Query("DELETE FROM season_standings WHERE season_id = :seasonId")
    suspend fun deleteStandings(seasonId: Long)

    @Query(
        """
        SELECT * FROM season_standings
        WHERE season_id = :seasonId
        ORDER BY season_rank ASC, points DESC, player_id ASC
        """,
    )
    fun observeStandings(seasonId: Long): Flow<List<SeasonStandingEntity>>

    @Query(
        """
        SELECT * FROM season_standings
        WHERE season_id = :seasonId
        ORDER BY season_rank ASC, points DESC, player_id ASC
        """,
    )
    suspend fun getStandings(seasonId: Long): List<SeasonStandingEntity>

    @Query(
        """
        SELECT * FROM season_standings
        WHERE season_id = :seasonId AND is_payout_eligible = 1
        ORDER BY season_rank ASC, points DESC, player_id ASC
        """,
    )
    suspend fun getPayoutEligibleStandings(seasonId: Long): List<SeasonStandingEntity>

    @Query(
        """
        SELECT * FROM season_standings
        WHERE season_id = :seasonId
        ORDER BY rebuys DESC, add_ons DESC, player_id ASC
        """,
    )
    suspend fun getFishStandings(seasonId: Long): List<SeasonStandingEntity>

    @Query(
        """
        SELECT * FROM season_standings
        WHERE season_id = :seasonId
        ORDER BY knockouts_for DESC, points DESC, player_id ASC
        """,
    )
    suspend fun getSniperStandings(seasonId: Long): List<SeasonStandingEntity>
}
