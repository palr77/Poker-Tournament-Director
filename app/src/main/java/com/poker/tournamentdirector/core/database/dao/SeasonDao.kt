package com.poker.tournamentdirector.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.poker.tournamentdirector.core.database.entity.BlindLevelEntity
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
    suspend fun getSeason(seasonId: Long): SeasonEntity?

    @Upsert
    suspend fun upsertSeason(season: SeasonEntity)

    @Query("UPDATE seasons SET is_active = 0 WHERE id != :seasonId")
    suspend fun deactivateOtherSeasons(seasonId: Long)

    @Upsert
    suspend fun upsertSeasonDates(dates: List<SeasonDateEntity>)

    @Query("SELECT * FROM season_dates WHERE season_id = :seasonId ORDER BY display_order ASC")
    fun observeSeasonDates(seasonId: Long): Flow<List<SeasonDateEntity>>

    @Upsert
    suspend fun upsertRegistration(registration: SeasonPlayerRegistrationEntity)

    @Query("SELECT * FROM season_player_registrations WHERE season_id = :seasonId")
    fun observeRegistrations(seasonId: Long): Flow<List<SeasonPlayerRegistrationEntity>>

    @Upsert
    suspend fun upsertBlindLevels(levels: List<BlindLevelEntity>)

    @Query("SELECT * FROM blind_levels WHERE season_id = :seasonId ORDER BY level_number ASC")
    fun observeBlindLevels(seasonId: Long): Flow<List<BlindLevelEntity>>

    @Upsert
    suspend fun upsertStandings(standings: List<SeasonStandingEntity>)

    @Query("SELECT * FROM season_standings WHERE season_id = :seasonId ORDER BY season_rank ASC")
    fun observeStandings(seasonId: Long): Flow<List<SeasonStandingEntity>>
}
