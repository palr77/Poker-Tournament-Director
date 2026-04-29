package com.poker.tournamentdirector.data.repository

import androidx.room.withTransaction
import com.poker.tournamentdirector.core.database.TournamentDirectorDatabase
import com.poker.tournamentdirector.core.database.dao.BountyDao
import com.poker.tournamentdirector.core.database.dao.NightDao
import com.poker.tournamentdirector.core.database.dao.PlayerDao
import com.poker.tournamentdirector.core.database.dao.SeasonDao
import com.poker.tournamentdirector.core.database.entity.BountyEventEntity
import com.poker.tournamentdirector.core.database.entity.BountyLedgerEntity
import com.poker.tournamentdirector.core.database.entity.ClockStateEntity
import com.poker.tournamentdirector.core.database.entity.GuestParticipationEntity
import com.poker.tournamentdirector.core.database.entity.NightActionEventEntity
import com.poker.tournamentdirector.core.database.entity.NightEntity
import com.poker.tournamentdirector.core.database.entity.NightPlayerStateEntity
import com.poker.tournamentdirector.data.mapper.toDomain
import com.poker.tournamentdirector.data.mapper.toEntity
import com.poker.tournamentdirector.domain.model.ActionType
import com.poker.tournamentdirector.domain.model.ClockState
import com.poker.tournamentdirector.domain.model.ClockStatus
import com.poker.tournamentdirector.domain.model.EliminationReason
import com.poker.tournamentdirector.domain.model.GuestParticipation
import com.poker.tournamentdirector.domain.model.Night
import com.poker.tournamentdirector.domain.model.NightActionEvent
import com.poker.tournamentdirector.domain.model.NightPlayerState
import com.poker.tournamentdirector.domain.model.NightPlayerStatus
import com.poker.tournamentdirector.domain.model.NightResult
import com.poker.tournamentdirector.domain.model.NightStatus
import com.poker.tournamentdirector.domain.model.PlayerKind
import com.poker.tournamentdirector.domain.model.SeasonStanding
import com.poker.tournamentdirector.domain.model.NightSummary
import com.poker.tournamentdirector.domain.repository.NightRepository
import com.poker.tournamentdirector.domain.rules.TournamentBusinessRules
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomNightRepository @Inject constructor(
    private val database: TournamentDirectorDatabase,
    private val nightDao: NightDao,
    private val seasonDao: SeasonDao,
    private val playerDao: PlayerDao,
    private val bountyDao: BountyDao,
) : NightRepository {
    override fun observeNightsForSeason(seasonId: Long): Flow<List<NightSummary>> =
        nightDao.observeNightsForSeason(seasonId).map { nights ->
            nights.map { it.toDomain().toSummary() }
        }

    override suspend fun getNightsForSeason(seasonId: Long): List<Night> =
        nightDao.getNightsForSeason(seasonId).map { it.toDomain() }

    override suspend fun getClosedNightsForSeason(seasonId: Long): List<Night> =
        nightDao.getClosedNightsForSeason(seasonId).map { it.toDomain() }

    override fun observeNight(nightId: Long): Flow<Night?> =
        nightDao.observeNight(nightId).map { night -> night?.toDomain() }

    override suspend fun getNight(nightId: Long): Night? =
        nightDao.getNight(nightId)?.toDomain()

    override suspend fun startNightFromSeasonDate(
        seasonId: Long,
        seasonDateId: Long,
        startedAt: Instant,
    ): Long =
        database.withTransaction {
            nightDao.getNightForSeasonDate(seasonId, seasonDateId)?.let { existing ->
                nightDao.markNightStarted(existing.id, startedAt = startedAt)
                return@withTransaction existing.id
            }

            val seasonDate = requireNotNull(seasonDao.getSeasonDate(seasonDateId)) {
                "Season date $seasonDateId does not exist."
            }
            val season = requireNotNull(seasonDao.getSeason(seasonId)) {
                "Season $seasonId does not exist."
            }
            val nightId = nightDao.insertNight(
                NightEntity(
                    seasonId = seasonId,
                    seasonDateId = seasonDateId,
                    nightNumber = seasonDate.displayOrder,
                    scheduledDate = seasonDate.date,
                    status = NightStatus.Active,
                    startedAt = startedAt,
                ),
            )

            val bountyPlayerIds = TournamentBusinessRules.bountyPlayerIdsAtNightStart(
                seasonDao.getStandings(seasonId).map { it.toDomain() },
            )
            seasonDao.getSeasonRoster(seasonId).forEach { player ->
                nightDao.insertPlayerState(
                    NightPlayerStateEntity(
                        nightId = nightId,
                        playerId = player.id,
                        isBountyPlayer = player.id in bountyPlayerIds,
                        isGuest = player.kind == PlayerKind.Guest,
                        isPayoutEligible = TournamentBusinessRules.isSeasonPayoutEligible(
                            playerKind = player.kind,
                            registered = true,
                        ),
                    ),
                )
            }
            val firstBlind = seasonDao.getBlindLevel(seasonId, levelNumber = 1)
            nightDao.upsertClockState(
                ClockStateEntity(
                    nightId = nightId,
                    currentLevelNumber = 1,
                    remainingSeconds = firstBlind?.durationSeconds ?: 0,
                    status = ClockStatus.Ready,
                ),
            )
            if (season.isActive) {
                seasonDao.setActiveSeason(seasonId)
            }

            nightId
        }

    override suspend fun createNight(night: Night): Long =
        nightDao.insertNight(night.copy(id = 0).toEntity())

    override suspend fun markNightStarted(
        nightId: Long,
        startedAt: Instant,
    ) {
        nightDao.markNightStarted(nightId = nightId, startedAt = startedAt)
    }

    override suspend fun addPlayerToNight(state: NightPlayerState): Long =
        nightDao.insertPlayerState(state.copy(id = 0).toEntity())

    override suspend fun addGuestToNight(
        nightId: Long,
        guestPlayerId: Long,
        replacingPlayerId: Long?,
    ) {
        database.withTransaction {
            val player = requireNotNull(playerDao.getPlayer(guestPlayerId)) {
                "Guest player $guestPlayerId does not exist."
            }
            TournamentBusinessRules.validateGuestParticipation(
                guestPlayerId = guestPlayerId,
                replacingPlayerId = replacingPlayerId,
                guestKind = player.kind,
            )
            replacingPlayerId?.let { replacementId ->
                val replacedState = requirePlayerState(nightId, replacementId)
                require(!replacedState.isGuest) {
                    "A guest can only replace a registered league player."
                }
            }
            nightDao.upsertPlayerState(
                NightPlayerStateEntity(
                    nightId = nightId,
                    playerId = guestPlayerId,
                    isGuest = true,
                    isPayoutEligible = false,
                ),
            )
            nightDao.upsertGuestParticipation(
                GuestParticipationEntity(
                    nightId = nightId,
                    guestPlayerId = guestPlayerId,
                    replacingPlayerId = replacingPlayerId,
                ),
            )
        }
    }

    override fun observePlayerStates(nightId: Long): Flow<List<NightPlayerState>> =
        nightDao.observePlayerStates(nightId).map { states -> states.map { it.toDomain() } }

    override suspend fun getPlayerStates(nightId: Long): List<NightPlayerState> =
        nightDao.getPlayerStates(nightId).map { it.toDomain() }

    override suspend fun recordBuyIn(
        nightId: Long,
        playerId: Long,
        amountCents: Long,
    ) {
        database.withTransaction {
            val state = requirePlayerState(nightId, playerId)
            TournamentBusinessRules.validateBuyIn(state.toDomain())
            nightDao.updatePlayerState(
                state.copy(
                    status = NightPlayerStatus.Active,
                    buyInCount = state.buyInCount + 1,
                ),
            )
            nightDao.insertAction(
                moneyAction(
                    nightId = nightId,
                    playerId = playerId,
                    action = ActionType.BuyIn,
                    amountCents = amountCents,
                    isBountySegment = state.isBountyPlayer,
                ),
            )
            updateGuestMoney(state, buyInDelta = amountCents)
        }
    }

    override suspend fun recordRebuy(
        nightId: Long,
        playerId: Long,
        amountCents: Long,
    ) {
        database.withTransaction {
            val state = requirePlayerState(nightId, playerId)
            TournamentBusinessRules.validateRebuy(state.toDomain())
            nightDao.updatePlayerState(
                state.copy(
                    status = NightPlayerStatus.Active,
                    rebuyCount = state.rebuyCount + 1,
                    eliminatedByPlayerId = null,
                    eliminatedAt = null,
                ),
            )
            nightDao.insertAction(
                moneyAction(
                    nightId = nightId,
                    playerId = playerId,
                    action = ActionType.Rebuy,
                    amountCents = amountCents,
                    isBountySegment = state.isBountyPlayer,
                ),
            )
            updateGuestMoney(state, rebuyDelta = amountCents)
        }
    }

    override suspend fun recordAddOn(
        nightId: Long,
        playerId: Long,
        amountCents: Long,
    ) {
        database.withTransaction {
            val state = requirePlayerState(nightId, playerId)
            TournamentBusinessRules.validateAddOn(state.toDomain())
            nightDao.updatePlayerState(state.copy(hasAddOn = true))
            nightDao.insertAction(
                moneyAction(
                    nightId = nightId,
                    playerId = playerId,
                    action = ActionType.AddOn,
                    amountCents = amountCents,
                    isBountySegment = state.isBountyPlayer,
                ),
            )
            updateGuestMoney(state, addOnDelta = amountCents)
        }
    }

    override suspend fun recordElimination(
        nightId: Long,
        playerId: Long,
        eliminatedByPlayerId: Long,
        at: Instant,
    ) {
        database.withTransaction {
            TournamentBusinessRules.validateElimination(playerId, eliminatedByPlayerId)
            val night = requireNotNull(nightDao.getNight(nightId)) { "Night $nightId does not exist." }
            val targetState = requirePlayerState(nightId, playerId)
            requirePlayerState(nightId, eliminatedByPlayerId)

            val finalBountyElimination = TournamentBusinessRules.isBountyFinalElimination(
                targetState.toDomain(),
            )
            nightDao.updatePlayerState(
                targetState.copy(
                    status = NightPlayerStatus.Eliminated,
                    eliminatedByPlayerId = eliminatedByPlayerId,
                    eliminatedAt = at,
                ),
            )
            nightDao.insertAction(
                NightActionEventEntity(
                    nightId = nightId,
                    playerId = eliminatedByPlayerId,
                    targetPlayerId = playerId,
                    action = ActionType.Elimination,
                    eliminationReason = if (finalBountyElimination) {
                        EliminationReason.Bounty
                    } else {
                        EliminationReason.Standard
                    },
                    createdAt = at,
                ),
            )

            if (finalBountyElimination &&
                bountyDao.getBountyEventForPlayer(nightId, playerId) == null
            ) {
                val amountCents = nightDao.getBountySegmentForPlayerCents(nightId, playerId)
                bountyDao.insertBountyEvent(
                    BountyEventEntity(
                        nightId = nightId,
                        bountyPlayerId = playerId,
                        collectedByPlayerId = eliminatedByPlayerId,
                        amountCents = amountCents,
                        createdAt = at,
                    ),
                )
                applyBountyLedger(
                    seasonId = night.seasonId,
                    winnerPlayerId = eliminatedByPlayerId,
                    bountyPlayerId = playerId,
                    amountCents = amountCents,
                )
            }
        }
    }

    override suspend fun assignSeats(
        nightId: Long,
        dateNumber: Int,
    ) {
        database.withTransaction {
            val night = requireNotNull(nightDao.getNight(nightId)) { "Night $nightId does not exist." }
            val states = nightDao.getPlayerStates(nightId)
            val standings = seasonDao.getStandings(night.seasonId).map { it.toDomain() }
            val seats = TournamentBusinessRules.assignSeats(
                dateNumber = dateNumber,
                playerIds = states.map { it.playerId },
                standings = standings,
            )
            nightDao.upsertPlayerStates(states.map { it.copy(seatNumber = null) })
            nightDao.upsertPlayerStates(
                states.map { state -> state.copy(seatNumber = seats[state.playerId]) },
            )
        }
    }

    override fun observeActions(nightId: Long): Flow<List<NightActionEvent>> =
        nightDao.observeActions(nightId).map { actions -> actions.map { it.toDomain() } }

    override suspend fun getActions(nightId: Long): List<NightActionEvent> =
        nightDao.getActions(nightId).map { it.toDomain() }

    override fun observeNightPotCents(nightId: Long): Flow<Long> =
        nightDao.observeNightPotCents(nightId)

    override suspend fun getNightPotCents(nightId: Long): Long =
        nightDao.getNightPotCents(nightId)

    override fun observeBountySegmentCents(nightId: Long): Flow<Long> =
        nightDao.observeBountySegmentCents(nightId)

    override suspend fun getBountySegmentCents(nightId: Long): Long =
        nightDao.getBountySegmentCents(nightId)

    override fun observePlayersLeft(nightId: Long): Flow<Int> =
        nightDao.observePlayersLeft(nightId)

    override fun observeClockState(nightId: Long): Flow<ClockState?> =
        nightDao.observeClockState(nightId).map { state -> state?.toDomain() }

    override suspend fun getClockState(nightId: Long): ClockState? =
        nightDao.getClockState(nightId)?.toDomain()

    override suspend fun updateClockState(state: ClockState) {
        nightDao.upsertClockState(state.toEntity())
    }

    override fun observeNightResults(nightId: Long): Flow<List<NightResult>> =
        nightDao.observeNightResults(nightId).map { results -> results.map { it.toDomain() } }

    override suspend fun getNightResults(nightId: Long): List<NightResult> =
        nightDao.getNightResults(nightId).map { it.toDomain() }

    override suspend fun getLifetimePoints(playerId: Long): Int =
        nightDao.getLifetimePoints(playerId)

    override suspend fun getResultHistoryForPlayer(playerId: Long): List<NightResult> =
        nightDao.getResultHistoryForPlayer(playerId).map { it.toDomain() }

    override suspend fun closeNight(
        nightId: Long,
        placements: List<Long>,
        closedAt: Instant,
    ) {
        database.withTransaction {
            val night = requireNotNull(nightDao.getNight(nightId)) { "Night $nightId does not exist." }
            val statesByPlayer = nightDao.getPlayerStates(nightId).associateBy { it.playerId }
            val nightPayouts = TournamentBusinessRules.calculateNightPayouts(
                nightDao.getNightPotCents(nightId),
            )
            val results = placements.mapIndexed { index, playerId ->
                val place = index + 1
                val state = requireNotNull(statesByPlayer[playerId]) {
                    "Player $playerId is not part of night $nightId."
                }
                NightResult(
                    nightId = nightId,
                    playerId = playerId,
                    place = place,
                    points = TournamentBusinessRules.pointsForPlace(place),
                    payoutCents = nightPayouts[place] ?: 0,
                    bountyPayoutCents = bountyDao.getBountyPayoutForPlayer(nightId, playerId),
                    playerKind = if (state.isGuest) PlayerKind.Guest else PlayerKind.League,
                    isPayoutEligible = state.isPayoutEligible,
                ).toEntity()
            }

            nightDao.deleteNightResults(nightId)
            nightDao.insertNightResults(results)
            nightDao.upsertPlayerStates(
                statesByPlayer.values.map { state ->
                    val place = placements.indexOf(state.playerId).takeIf { it >= 0 }?.plus(1)
                    state.copy(
                        status = when (place) {
                            1 -> NightPlayerStatus.Winner
                            null -> state.status
                            else -> NightPlayerStatus.Eliminated
                        },
                        finalPlace = place,
                    )
                },
            )
            results.forEach { result ->
                if (result.playerKind == PlayerKind.Guest) {
                    val existing = nightDao.getGuestParticipation(nightId, result.playerId)
                    if (existing != null) {
                        nightDao.upsertGuestParticipation(
                            existing.copy(pointsEarned = result.points, place = result.place),
                        )
                    }
                }
            }
            nightDao.markNightClosed(nightId = nightId, closedAt = closedAt)
            rebuildStandingsInTransaction(night.seasonId)
        }
    }

    override fun observeGuestParticipations(nightId: Long): Flow<List<GuestParticipation>> =
        nightDao.observeGuestParticipations(nightId).map { guests -> guests.map { it.toDomain() } }

    override suspend fun getGuestParticipations(nightId: Long): List<GuestParticipation> =
        nightDao.getGuestParticipations(nightId).map { it.toDomain() }

    private suspend fun requirePlayerState(
        nightId: Long,
        playerId: Long,
    ): NightPlayerStateEntity =
        requireNotNull(nightDao.getPlayerState(nightId, playerId)) {
            "Player $playerId is not part of night $nightId."
        }

    private fun moneyAction(
        nightId: Long,
        playerId: Long,
        action: ActionType,
        amountCents: Long,
        isBountySegment: Boolean,
    ): NightActionEventEntity =
        NightActionEventEntity(
            nightId = nightId,
            playerId = playerId,
            action = action,
            amountCents = amountCents,
            isBountySegment = isBountySegment,
        )

    private suspend fun updateGuestMoney(
        state: NightPlayerStateEntity,
        buyInDelta: Long = 0,
        rebuyDelta: Long = 0,
        addOnDelta: Long = 0,
    ) {
        if (!state.isGuest) return

        val existing = nightDao.getGuestParticipation(state.nightId, state.playerId)
        nightDao.upsertGuestParticipation(
            existing?.copy(
                buyInCents = existing.buyInCents + buyInDelta,
                rebuysCents = existing.rebuysCents + rebuyDelta,
                addOnCents = existing.addOnCents + addOnDelta,
            ) ?: GuestParticipationEntity(
                nightId = state.nightId,
                guestPlayerId = state.playerId,
                buyInCents = buyInDelta,
                rebuysCents = rebuyDelta,
                addOnCents = addOnDelta,
            ),
        )
    }

    private suspend fun applyBountyLedger(
        seasonId: Long,
        winnerPlayerId: Long,
        bountyPlayerId: Long,
        amountCents: Long,
    ) {
        val winnerLedger = bountyDao.getLedger(seasonId, winnerPlayerId)
            ?: BountyLedgerEntity(seasonId = seasonId, playerId = winnerPlayerId)
        val loserLedger = bountyDao.getLedger(seasonId, bountyPlayerId)
            ?: BountyLedgerEntity(seasonId = seasonId, playerId = bountyPlayerId)

        bountyDao.upsertLedger(
            winnerLedger.copy(
                bountiesWon = winnerLedger.bountiesWon + 1,
                amountWonCents = winnerLedger.amountWonCents + amountCents,
            ),
        )
        bountyDao.upsertLedger(
            loserLedger.copy(
                bountiesLost = loserLedger.bountiesLost + 1,
                amountLostCents = loserLedger.amountLostCents + amountCents,
            ),
        )
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
            val buyInCount = playerActions.count { it.action == ActionType.BuyIn }

            SeasonStanding(
                seasonId = seasonId,
                playerId = playerId,
                points = playerResults.sumOf { it.points },
                nightsPlayed = buyInCount.coerceAtLeast(playerResults.size),
                wins = playerResults.count { it.place == 1 },
                topFourFinishes = playerResults.count { it.place in 1..4 },
                knockoutsFor = playerActions.count { it.action == ActionType.Elimination },
                knockoutsAgainst = actions.count {
                    it.action == ActionType.Elimination && it.targetPlayerId == playerId
                },
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
        seasonDao.deleteStandings(seasonId)
        seasonDao.upsertStandings(
            ranked.map { standing ->
                standing.copy(seasonPayoutCents = payouts[standing.playerId] ?: 0).toEntity()
            },
        )
    }
}

private fun Night.toSummary(): NightSummary =
    NightSummary(
        id = id,
        seasonId = seasonId,
        nightNumber = nightNumber,
        statusLabel = status.name,
    )
