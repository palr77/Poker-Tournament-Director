package com.poker.tournamentdirector.domain.rules

import com.poker.tournamentdirector.domain.model.NightPlayerState
import com.poker.tournamentdirector.domain.model.NightPlayerStatus
import com.poker.tournamentdirector.domain.model.NightResult
import com.poker.tournamentdirector.domain.model.PlayerKind
import com.poker.tournamentdirector.domain.model.SeasonStanding
import kotlin.math.roundToLong

object TournamentBusinessRules {
    const val DEFAULT_SEASON_DATES = 10
    const val DEFAULT_INSCRIPTION_FEE_CENTS = 200_000L
    const val DEFAULT_BUY_IN_CENTS = 20_000L
    const val DEFAULT_REBUY_CENTS = 20_000L
    const val DEFAULT_ADD_ON_CENTS = 20_000L
    const val MAX_REBUYS_PER_NIGHT = 2
    const val POINTS_FIRST = 9
    const val POINTS_SECOND = 6
    const val POINTS_THIRD = 3
    const val POINTS_FOURTH = 1
    const val BREAK_LEVEL = 5
    const val ANTE_START_LEVEL = 6
    const val ANTE_BIG_BLIND_PERCENT = 20

    private val nightPointSchedule = mapOf(
        1 to POINTS_FIRST,
        2 to POINTS_SECOND,
        3 to POINTS_THIRD,
        4 to POINTS_FOURTH,
    )

    fun canBuyIn(state: NightPlayerState): Boolean =
        state.buyInCount == 0 &&
            state.status != NightPlayerStatus.Eliminated &&
            state.status != NightPlayerStatus.Winner

    fun canRebuy(state: NightPlayerState): Boolean =
        state.buyInCount > 0 &&
            state.status != NightPlayerStatus.Winner &&
            state.rebuyCount < MAX_REBUYS_PER_NIGHT

    fun canAddOn(state: NightPlayerState): Boolean =
        !state.hasAddOn &&
            state.buyInCount > 0 &&
            state.status != NightPlayerStatus.Eliminated &&
            state.status != NightPlayerStatus.Winner &&
            state.rebuyCount < MAX_REBUYS_PER_NIGHT

    fun validateBuyIn(state: NightPlayerState) {
        require(canBuyIn(state)) {
            "A player can buy in only once per night."
        }
    }

    fun validateRebuy(state: NightPlayerState) {
        require(canRebuy(state)) {
            "A player can rebuy at most $MAX_REBUYS_PER_NIGHT times per night."
        }
    }

    fun validateAddOn(state: NightPlayerState) {
        require(canAddOn(state)) {
            "Add-on is allowed only before the player has used $MAX_REBUYS_PER_NIGHT rebuys."
        }
    }

    fun validateElimination(
        playerId: Long,
        eliminatedByPlayerId: Long?,
    ) {
        requireNotNull(eliminatedByPlayerId) {
            "Eliminated-by attribution is required."
        }
        require(playerId != eliminatedByPlayerId) {
            "A player cannot eliminate themselves."
        }
    }

    fun validateGuestParticipation(
        guestPlayerId: Long,
        replacingPlayerId: Long?,
        guestKind: PlayerKind,
    ) {
        require(guestKind == PlayerKind.Guest) {
            "Guest participation requires a player marked as Guest."
        }
        require(guestPlayerId != replacingPlayerId) {
            "A guest cannot replace themselves."
        }
    }

    fun validateSeasonRegistration(playerKind: PlayerKind) {
        require(playerKind == PlayerKind.League) {
            "Guest players are night-scoped and cannot be registered on a season roster."
        }
    }

    fun isSeasonPayoutEligible(
        playerKind: PlayerKind,
        registered: Boolean,
    ): Boolean = registered && playerKind == PlayerKind.League

    fun bountyPlayerIdsAtNightStart(standings: List<SeasonStanding>): Set<Long> {
        val eligibleStandings = standings
            .filter { it.isPayoutEligible && it.playerKind == PlayerKind.League && it.points > 0 }
        val topPoints = eligibleStandings.maxOfOrNull { it.points } ?: return emptySet()

        return eligibleStandings
            .filter { it.points == topPoints }
            .mapTo(mutableSetOf()) { it.playerId }
    }

    fun isBountyFinalElimination(targetState: NightPlayerState): Boolean =
        targetState.isBountyPlayer && targetState.rebuyCount >= MAX_REBUYS_PER_NIGHT

    fun assignNightPoints(results: List<NightResult>): Map<Long, Int> =
        results.associate { result ->
            result.playerId to pointsForPlace(result.place)
        }

    fun pointsForPlace(place: Int): Int = nightPointSchedule[place] ?: 0

    fun calculateNightPayouts(nightPotCents: Long): Map<Int, Long> =
        splitCents(
            totalCents = nightPotCents,
            sharesByKey = linkedMapOf(1 to 60, 2 to 30, 3 to 10),
        )

    fun calculateSeasonPayouts(
        payoutEligibleStandings: List<SeasonStanding>,
        seasonPoolCents: Long,
    ): Map<Long, Long> {
        val ranked = payoutEligibleStandings
            .filter { it.isPayoutEligible && it.playerKind == PlayerKind.League }
            .sortedWith(compareByDescending<SeasonStanding> { it.points }.thenBy { it.playerId })
        if (ranked.isEmpty() || seasonPoolCents <= 0) return emptyMap()

        val groups = ranked
            .groupBy { it.points }
            .toList()
            .sortedByDescending { (points, _) -> points }
            .map { (_, standings) -> standings.sortedBy { it.playerId } }

        val first = groups.getOrNull(0).orEmpty()
        val second = groups.getOrNull(1).orEmpty()
        val third = groups.getOrNull(2).orEmpty()
        val thirdWhenFirstIsTied = groups.getOrNull(1).orEmpty()

        return when {
            first.size > 1 -> buildMap {
                val leaderShare = percentOf(seasonPoolCents, 90) / first.size
                first.forEach { put(it.playerId, leaderShare) }
                thirdWhenFirstIsTied.firstOrNull()?.let {
                    put(it.playerId, percentOf(seasonPoolCents, 10))
                }
            }

            second.size > 1 -> buildMap {
                first.firstOrNull()?.let { put(it.playerId, percentOf(seasonPoolCents, 60)) }
                val tiedSecondShare = percentOf(seasonPoolCents, 40) / second.size
                second.forEach { put(it.playerId, tiedSecondShare) }
            }

            else -> buildMap {
                first.firstOrNull()?.let { put(it.playerId, percentOf(seasonPoolCents, 60)) }
                second.firstOrNull()?.let { put(it.playerId, percentOf(seasonPoolCents, 30)) }
                third.firstOrNull()?.let { put(it.playerId, percentOf(seasonPoolCents, 10)) }
            }
        }
    }

    fun rankStandings(standings: List<SeasonStanding>): List<SeasonStanding> {
        var previousPoints: Int? = null
        var previousRank = 0

        return standings
            .sortedWith(compareByDescending<SeasonStanding> { it.points }.thenBy { it.playerId })
            .mapIndexed { index, standing ->
                val rank = if (standing.points == previousPoints) {
                    previousRank
                } else {
                    index + 1
                }
                previousPoints = standing.points
                previousRank = rank
                standing.copy(seasonRank = rank)
            }
    }

    fun assignSeats(
        dateNumber: Int,
        playerIds: List<Long>,
        standings: List<SeasonStanding>,
        randomizeFirstDate: (List<Long>) -> List<Long> = { it.shuffled() },
    ): Map<Long, Int?> {
        if (dateNumber <= 1) {
            return randomizeFirstDate(playerIds).mapIndexed { index, playerId ->
                playerId to index + 1
            }.toMap()
        }

        val standingsByPlayer = standings.associateBy { it.playerId }
        val rankedPlayers = playerIds
            .filter { playerId -> (standingsByPlayer[playerId]?.points ?: 0) > 0 }
            .sortedWith(
                compareBy<Long> { standingsByPlayer[it]?.seasonRank ?: Int.MAX_VALUE }
                    .thenByDescending { standingsByPlayer[it]?.points ?: 0 }
                    .thenBy { it },
            )
        val freeChoicePlayers = playerIds.filterNot { it in rankedPlayers }

        return buildMap {
            rankedPlayers.forEachIndexed { index, playerId -> put(playerId, index + 1) }
            freeChoicePlayers.forEach { playerId -> put(playerId, null) }
        }
    }

    fun anteForLevel(
        levelNumber: Int,
        bigBlind: Int,
        isBreak: Boolean = levelNumber == BREAK_LEVEL,
    ): Int =
        if (isBreak || levelNumber < ANTE_START_LEVEL) {
            0
        } else {
            (bigBlind * ANTE_BIG_BLIND_PERCENT) / 100
        }

    fun guestImpactPoints(results: List<NightResult>): Int =
        results
            .filter { it.playerKind == PlayerKind.Guest }
            .sumOf { pointsForPlace(it.place) }

    private fun splitCents(
        totalCents: Long,
        sharesByKey: LinkedHashMap<Int, Int>,
    ): Map<Int, Long> {
        if (totalCents <= 0) return sharesByKey.keys.associateWith { 0L }

        var allocated = 0L
        return sharesByKey.entries.mapIndexed { index, entry ->
            val amount = if (index == sharesByKey.size - 1) {
                totalCents - allocated
            } else {
                percentOf(totalCents, entry.value).also { allocated += it }
            }
            entry.key to amount
        }.toMap()
    }

    private fun percentOf(
        totalCents: Long,
        percent: Int,
    ): Long = ((totalCents * percent) / 100.0).roundToLong()
}
