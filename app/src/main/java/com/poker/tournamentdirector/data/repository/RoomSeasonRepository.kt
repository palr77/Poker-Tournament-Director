package com.poker.tournamentdirector.data.repository

import androidx.room.withTransaction
import com.poker.tournamentdirector.core.database.TournamentDirectorDatabase
import com.poker.tournamentdirector.core.database.dao.NightDao
import com.poker.tournamentdirector.core.database.dao.PlayerDao
import com.poker.tournamentdirector.core.database.dao.SeasonDao
import com.poker.tournamentdirector.core.database.entity.SeasonPlayerRegistrationEntity
import com.poker.tournamentdirector.data.mapper.toDomain
import com.poker.tournamentdirector.data.mapper.toEntity
import com.poker.tournamentdirector.domain.model.ActionType
import com.poker.tournamentdirector.domain.model.BlindLevel
import com.poker.tournamentdirector.domain.model.Player
import com.poker.tournamentdirector.domain.model.PlayerKind
import com.poker.tournamentdirector.domain.model.Season
import com.poker.tournamentdirector.domain.model.SeasonDate
import com.poker.tournamentdirector.domain.model.SeasonPlayerRegistration
import com.poker.tournamentdirector.domain.model.SeasonStanding
import com.poker.tournamentdirector.domain.model.SeasonSummary
import com.poker.tournamentdirector.domain.repository.SeasonRepository
import com.poker.tournamentdirector.domain.rules.TournamentBusinessRules
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomSeasonRepository @Inject constructor(
    private val database: TournamentDirectorDatabase,
    private val seasonDao: SeasonDao,
    private val nightDao: NightDao,
    private val playerDao: PlayerDao,
) : SeasonRepository {
    override fun observeSeasons(): Flow<List<SeasonSummary>> =
        seasonDao.observeSeasons().map { seasons -> seasons.map { it.toDomain().toSummary() } }

    override fun observeActiveSeason(): Flow<SeasonSummary?> =
        seasonDao.observeActiveSeason().map { season -> season?.toDomain()?.toSummary() }

    override fun observeActiveSeasonDetails(): Flow<Season?> =
        seasonDao.observeActiveSeason().map { season -> season?.toDomain() }

    override fun observeSeason(seasonId: Long): Flow<Season?> =
        seasonDao.observeSeason(seasonId).map { season -> season?.toDomain() }

    override suspend fun getSeason(seasonId: Long): Season? =
        seasonDao.getSeason(seasonId)?.toDomain()

    override suspend fun createSeason(season: Season): Long =
        database.withTransaction {
            val seasonId = seasonDao.insertSeason(season.copy(id = 0).toEntity())
            if (season.isActive) {
                seasonDao.setActiveSeason(seasonId)
            }
            seasonId
        }

    override suspend fun updateSeason(season: Season) {
        seasonDao.updateSeason(season.toEntity())
    }

    override suspend fun setActiveSeason(seasonId: Long) {
        seasonDao.setActiveSeason(seasonId)
    }

    override fun observeSeasonDates(seasonId: Long): Flow<List<SeasonDate>> =
        seasonDao.observeSeasonDates(seasonId).map { dates -> dates.map { it.toDomain() } }

    override suspend fun getSeasonDates(seasonId: Long): List<SeasonDate> =
        seasonDao.getSeasonDates(seasonId).map { it.toDomain() }

    override suspend fun replaceSeasonDates(
        seasonId: Long,
        dates: List<SeasonDate>,
    ) {
        database.withTransaction {
            seasonDao.deleteSeasonDates(seasonId)
            seasonDao.upsertSeasonDates(
                dates.map { date ->
                    date.copy(id = 0, seasonId = seasonId).toEntity()
                },
            )
        }
    }

    override suspend fun upsertSeasonDate(date: SeasonDate) {
        seasonDao.upsertSeasonDate(date.toEntity())
    }

    override suspend fun registerPlayer(
        seasonId: Long,
        playerId: Long,
        inscriptionPaidCents: Long,
        isPayoutEligible: Boolean,
    ): Long =
        database.withTransaction {
            val player = requireNotNull(playerDao.getPlayer(playerId)) {
                "Player $playerId does not exist."
            }
            TournamentBusinessRules.validateSeasonRegistration(player.kind)
            val existing = seasonDao.getRegistration(seasonId, playerId)
            if (existing != null) {
                seasonDao.updateRegistration(
                    existing.copy(
                        inscriptionPaidCents = inscriptionPaidCents,
                        isPayoutEligible = isPayoutEligible,
                    ),
                )
                existing.id
            } else {
                seasonDao.insertRegistration(
                    SeasonPlayerRegistrationEntity(
                        seasonId = seasonId,
                        playerId = playerId,
                        registrationNumber = seasonDao.nextRegistrationNumber(seasonId),
                        inscriptionPaidCents = inscriptionPaidCents,
                        isPayoutEligible = isPayoutEligible,
                    ),
                )
            }
        }

    override suspend fun unregisterPlayer(
        seasonId: Long,
        playerId: Long,
    ) {
        seasonDao.getRegistration(seasonId, playerId)?.let { registration ->
            seasonDao.deleteRegistration(registration)
        }
    }

    override fun observeRegistrations(seasonId: Long): Flow<List<SeasonPlayerRegistration>> =
        seasonDao.observeRegistrations(seasonId).map { registrations ->
            registrations.map { it.toDomain() }
        }

    override suspend fun getRegistrations(seasonId: Long): List<SeasonPlayerRegistration> =
        seasonDao.getRegistrations(seasonId).map { it.toDomain() }

    override fun observeSeasonRoster(seasonId: Long): Flow<List<Player>> =
        seasonDao.observeSeasonRoster(seasonId).map { roster -> roster.map { it.toDomain() } }

    override suspend fun getSeasonRoster(seasonId: Long): List<Player> =
        seasonDao.getSeasonRoster(seasonId).map { it.toDomain() }

    override suspend fun getSeasonPoolCents(seasonId: Long): Long =
        seasonDao.getSeasonPoolCents(seasonId)

    override fun observeBlindLevels(seasonId: Long): Flow<List<BlindLevel>> =
        seasonDao.observeBlindLevels(seasonId).map { levels -> levels.map { it.toDomain() } }

    override suspend fun getBlindLevels(seasonId: Long): List<BlindLevel> =
        seasonDao.getBlindLevels(seasonId).map { it.toDomain() }

    override suspend fun replaceBlindLevels(
        seasonId: Long,
        levels: List<BlindLevel>,
    ) {
        database.withTransaction {
            seasonDao.deleteBlindLevels(seasonId)
            seasonDao.upsertBlindLevels(
                levels.map { level ->
                    val isBreak = level.isBreak ||
                        level.levelNumber == TournamentBusinessRules.BREAK_LEVEL
                    level.copy(
                        id = 0,
                        seasonId = seasonId,
                        isBreak = isBreak,
                        ante = TournamentBusinessRules.anteForLevel(
                            levelNumber = level.levelNumber,
                            bigBlind = level.bigBlind,
                            isBreak = isBreak,
                        ),
                    ).toEntity()
                },
            )
        }
    }

    override fun observeStandings(seasonId: Long): Flow<List<SeasonStanding>> =
        seasonDao.observeStandings(seasonId).map { standings -> standings.map { it.toDomain() } }

    override suspend fun getStandings(seasonId: Long): List<SeasonStanding> =
        seasonDao.getStandings(seasonId).map { it.toDomain() }

    override suspend fun getPayoutEligibleStandings(seasonId: Long): List<SeasonStanding> =
        seasonDao.getPayoutEligibleStandings(seasonId).map { it.toDomain() }

    override suspend fun getFishStandings(seasonId: Long): List<SeasonStanding> =
        seasonDao.getFishStandings(seasonId).map { it.toDomain() }

    override suspend fun getSniperStandings(seasonId: Long): List<SeasonStanding> =
        seasonDao.getSniperStandings(seasonId).map { it.toDomain() }

    override suspend fun rebuildStandings(seasonId: Long) {
        database.withTransaction {
            rebuildStandingsInTransaction(seasonId)
        }
    }

    private suspend fun rebuildStandingsInTransaction(seasonId: Long) {
        val registrations = seasonDao.getRegistrations(seasonId)
        val rosterByPlayer = seasonDao.getSeasonRoster(seasonId).associateBy { it.id }
        val nights = nightDao.getNightsForSeason(seasonId)
        val nightIds = nights.map { it.id }
        val results = if (nightIds.isEmpty()) {
            emptyList()
        } else {
            nightDao.getResultsForNights(nightIds)
        }
        val actions = if (nightIds.isEmpty()) {
            emptyList()
        } else {
            nightDao.getActionsForNights(nightIds)
        }

        val standings = registrations.map { registration ->
            val playerId = registration.playerId
            val player = rosterByPlayer[playerId]
            val playerResults = results.filter { it.playerId == playerId }
            val playerActions = actions.filter { it.playerId == playerId }
            val knockoutsAgainst = actions.count {
                it.action == ActionType.Elimination && it.targetPlayerId == playerId
            }
            val buyInCount = playerActions.count { it.action == ActionType.BuyIn }

            SeasonStanding(
                seasonId = seasonId,
                playerId = playerId,
                points = playerResults.sumOf { it.points },
                nightsPlayed = buyInCount.coerceAtLeast(playerResults.size),
                wins = playerResults.count { it.place == 1 },
                topFourFinishes = playerResults.count { it.place in 1..4 },
                knockoutsFor = playerActions.count { it.action == ActionType.Elimination },
                knockoutsAgainst = knockoutsAgainst,
                rebuys = playerActions.count { it.action == ActionType.Rebuy },
                addOns = playerActions.count { it.action == ActionType.AddOn },
                playerKind = player?.kind ?: PlayerKind.League,
                isPayoutEligible = registration.isPayoutEligible &&
                    TournamentBusinessRules.isSeasonPayoutEligible(
                        playerKind = player?.kind ?: PlayerKind.League,
                        registered = player != null,
                    ),
            )
        }

        val ranked = TournamentBusinessRules.rankStandings(standings)
        val payouts = TournamentBusinessRules.calculateSeasonPayouts(
            payoutEligibleStandings = ranked,
            seasonPoolCents = seasonDao.getSeasonPoolCents(seasonId),
        )
        val finalStandings = ranked.map { standing ->
            standing.copy(seasonPayoutCents = payouts[standing.playerId] ?: 0)
        }

        seasonDao.deleteStandings(seasonId)
        seasonDao.upsertStandings(finalStandings.map { it.toEntity() })
    }
}

private fun Season.toSummary(): SeasonSummary =
    SeasonSummary(
        id = id,
        name = name,
        isActive = isActive,
        buyInCents = buyInCents,
    )
