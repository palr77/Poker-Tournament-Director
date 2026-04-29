package com.poker.tournamentdirector.domain.repository

import com.poker.tournamentdirector.domain.model.BlindLevel
import com.poker.tournamentdirector.domain.model.Player
import com.poker.tournamentdirector.domain.model.Season
import com.poker.tournamentdirector.domain.model.SeasonDate
import com.poker.tournamentdirector.domain.model.SeasonPlayerRegistration
import com.poker.tournamentdirector.domain.model.SeasonStanding
import com.poker.tournamentdirector.domain.model.SeasonSummary
import kotlinx.coroutines.flow.Flow

interface SeasonRepository {
    fun observeSeasons(): Flow<List<SeasonSummary>>

    fun observeActiveSeason(): Flow<SeasonSummary?>

    fun observeActiveSeasonDetails(): Flow<Season?>

    fun observeSeason(seasonId: Long): Flow<Season?>

    suspend fun getSeason(seasonId: Long): Season?

    suspend fun createSeason(season: Season): Long

    suspend fun updateSeason(season: Season)

    suspend fun setActiveSeason(seasonId: Long)

    fun observeSeasonDates(seasonId: Long): Flow<List<SeasonDate>>

    suspend fun getSeasonDates(seasonId: Long): List<SeasonDate>

    suspend fun replaceSeasonDates(
        seasonId: Long,
        dates: List<SeasonDate>,
    )

    suspend fun upsertSeasonDate(date: SeasonDate)

    suspend fun registerPlayer(
        seasonId: Long,
        playerId: Long,
        inscriptionPaidCents: Long,
        isPayoutEligible: Boolean = true,
    ): Long

    suspend fun unregisterPlayer(
        seasonId: Long,
        playerId: Long,
    )

    fun observeRegistrations(seasonId: Long): Flow<List<SeasonPlayerRegistration>>

    suspend fun getRegistrations(seasonId: Long): List<SeasonPlayerRegistration>

    fun observeSeasonRoster(seasonId: Long): Flow<List<Player>>

    suspend fun getSeasonRoster(seasonId: Long): List<Player>

    suspend fun getSeasonPoolCents(seasonId: Long): Long

    fun observeBlindLevels(seasonId: Long): Flow<List<BlindLevel>>

    suspend fun getBlindLevels(seasonId: Long): List<BlindLevel>

    suspend fun replaceBlindLevels(
        seasonId: Long,
        levels: List<BlindLevel>,
    )

    fun observeStandings(seasonId: Long): Flow<List<SeasonStanding>>

    suspend fun getStandings(seasonId: Long): List<SeasonStanding>

    suspend fun getPayoutEligibleStandings(seasonId: Long): List<SeasonStanding>

    suspend fun getFishStandings(seasonId: Long): List<SeasonStanding>

    suspend fun getSniperStandings(seasonId: Long): List<SeasonStanding>

    suspend fun rebuildStandings(seasonId: Long)
}
