package com.poker.tournamentdirector.domain.repository

import com.poker.tournamentdirector.domain.model.ClockState
import com.poker.tournamentdirector.domain.model.GuestParticipation
import com.poker.tournamentdirector.domain.model.Night
import com.poker.tournamentdirector.domain.model.NightActionEvent
import com.poker.tournamentdirector.domain.model.NightPlayerState
import com.poker.tournamentdirector.domain.model.NightResult
import com.poker.tournamentdirector.domain.model.NightSummary
import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface NightRepository {
    fun observeNightsForSeason(seasonId: Long): Flow<List<NightSummary>>

    suspend fun getNightsForSeason(seasonId: Long): List<Night>

    suspend fun getClosedNightsForSeason(seasonId: Long): List<Night>

    fun observeNight(nightId: Long): Flow<Night?>

    suspend fun getNight(nightId: Long): Night?

    suspend fun startNightFromSeasonDate(
        seasonId: Long,
        seasonDateId: Long,
        startedAt: Instant = Instant.now(),
    ): Long

    suspend fun createNight(night: Night): Long

    suspend fun markNightStarted(
        nightId: Long,
        startedAt: Instant = Instant.now(),
    )

    suspend fun addPlayerToNight(state: NightPlayerState): Long

    suspend fun addGuestToNight(
        nightId: Long,
        guestPlayerId: Long,
        replacingPlayerId: Long? = null,
    )

    fun observePlayerStates(nightId: Long): Flow<List<NightPlayerState>>

    suspend fun getPlayerStates(nightId: Long): List<NightPlayerState>

    suspend fun recordBuyIn(
        nightId: Long,
        playerId: Long,
        amountCents: Long,
    )

    suspend fun recordRebuy(
        nightId: Long,
        playerId: Long,
        amountCents: Long,
    )

    suspend fun recordAddOn(
        nightId: Long,
        playerId: Long,
        amountCents: Long,
    )

    suspend fun recordElimination(
        nightId: Long,
        playerId: Long,
        eliminatedByPlayerId: Long,
        at: Instant = Instant.now(),
    )

    suspend fun assignSeats(
        nightId: Long,
        dateNumber: Int,
    )

    fun observeActions(nightId: Long): Flow<List<NightActionEvent>>

    suspend fun getActions(nightId: Long): List<NightActionEvent>

    fun observeNightPotCents(nightId: Long): Flow<Long>

    suspend fun getNightPotCents(nightId: Long): Long

    fun observeBountySegmentCents(nightId: Long): Flow<Long>

    suspend fun getBountySegmentCents(nightId: Long): Long

    fun observePlayersLeft(nightId: Long): Flow<Int>

    fun observeClockState(nightId: Long): Flow<ClockState?>

    suspend fun getClockState(nightId: Long): ClockState?

    suspend fun updateClockState(state: ClockState)

    fun observeNightResults(nightId: Long): Flow<List<NightResult>>

    suspend fun getNightResults(nightId: Long): List<NightResult>

    suspend fun getLifetimePoints(playerId: Long): Int

    suspend fun getResultHistoryForPlayer(playerId: Long): List<NightResult>

    suspend fun closeNight(
        nightId: Long,
        placements: List<Long>,
        closedAt: Instant = Instant.now(),
    )

    fun observeGuestParticipations(nightId: Long): Flow<List<GuestParticipation>>

    suspend fun getGuestParticipations(nightId: Long): List<GuestParticipation>
}
