package com.poker.tournamentdirector.domain.rules

import com.poker.tournamentdirector.domain.model.NightPlayerState
import com.poker.tournamentdirector.domain.model.NightPlayerStatus
import com.poker.tournamentdirector.domain.model.NightResult
import com.poker.tournamentdirector.domain.model.PlayerKind
import com.poker.tournamentdirector.domain.model.SeasonStanding
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TournamentBusinessRulesTest {
    @Test
    fun `rebuy and add-on stop after two rebuys`() {
        val oneRebuy = playerState(playerId = 10, rebuyCount = 1)
        val twoRebuys = playerState(playerId = 10, rebuyCount = 2)
        val notBoughtIn = playerState(playerId = 10, buyInCount = 0)

        assertTrue(TournamentBusinessRules.canRebuy(oneRebuy))
        assertTrue(TournamentBusinessRules.canAddOn(oneRebuy))
        assertFalse(TournamentBusinessRules.canRebuy(twoRebuys))
        assertFalse(TournamentBusinessRules.canAddOn(twoRebuys))
        assertFalse(TournamentBusinessRules.canRebuy(notBoughtIn))
        assertFalse(TournamentBusinessRules.canAddOn(notBoughtIn))
    }

    @Test
    fun `buy-in is allowed once per player per night`() {
        assertTrue(TournamentBusinessRules.canBuyIn(playerState(playerId = 10, buyInCount = 0)))
        assertFalse(TournamentBusinessRules.canBuyIn(playerState(playerId = 10, buyInCount = 1)))

        assertThrows(IllegalArgumentException::class.java) {
            TournamentBusinessRules.validateBuyIn(playerState(playerId = 10, buyInCount = 1))
        }
    }

    @Test
    fun `elimination attribution is required and cannot target self`() {
        assertThrows(IllegalArgumentException::class.java) {
            TournamentBusinessRules.validateElimination(playerId = 4, eliminatedByPlayerId = null)
        }

        assertThrows(IllegalArgumentException::class.java) {
            TournamentBusinessRules.validateElimination(playerId = 4, eliminatedByPlayerId = 4)
        }

        TournamentBusinessRules.validateElimination(playerId = 4, eliminatedByPlayerId = 9)
    }

    @Test
    fun `all tied season leaders are bounty players`() {
        val standings = listOf(
            standing(playerId = 1, points = 12, rank = 1),
            standing(playerId = 2, points = 12, rank = 1),
            standing(playerId = 3, points = 9, rank = 3),
            standing(playerId = 4, points = 14, rank = 0, isGuest = true),
        )

        val bountyIds = TournamentBusinessRules.bountyPlayerIdsAtNightStart(standings)

        assertEquals(setOf(1L, 2L), bountyIds)
    }

    @Test
    fun `bounty is collected only on final elimination after rebuys are depleted`() {
        assertFalse(
            TournamentBusinessRules.isBountyFinalElimination(
                playerState(playerId = 1, rebuyCount = 1, isBountyPlayer = true),
            ),
        )
        assertFalse(
            TournamentBusinessRules.isBountyFinalElimination(
                playerState(playerId = 2, rebuyCount = 2, isBountyPlayer = false),
            ),
        )
        assertTrue(
            TournamentBusinessRules.isBountyFinalElimination(
                playerState(playerId = 3, rebuyCount = 2, isBountyPlayer = true),
            ),
        )
    }

    @Test
    fun `guest points are recorded but not redistributed to lower registered players`() {
        val results = listOf(
            result(playerId = 100, place = 1, isGuest = true),
            result(playerId = 1, place = 2),
            result(playerId = 2, place = 3),
            result(playerId = 3, place = 4),
            result(playerId = 4, place = 5),
        )

        val pointsByPlayer = TournamentBusinessRules.assignNightPoints(results)

        assertEquals(9, pointsByPlayer[100])
        assertEquals(6, pointsByPlayer[1])
        assertEquals(3, pointsByPlayer[2])
        assertEquals(1, pointsByPlayer[3])
        assertEquals(0, pointsByPlayer[4])
    }

    @Test
    fun `guest players are not season payout eligible`() {
        assertTrue(
            TournamentBusinessRules.isSeasonPayoutEligible(
                PlayerKind.League,
                registered = true,
            ),
        )
        assertFalse(
            TournamentBusinessRules.isSeasonPayoutEligible(
                PlayerKind.League,
                registered = false,
            ),
        )
        assertFalse(
            TournamentBusinessRules.isSeasonPayoutEligible(
                PlayerKind.Guest,
                registered = true,
            ),
        )
    }

    @Test
    fun `guest players cannot be registered on the season roster`() {
        TournamentBusinessRules.validateSeasonRegistration(PlayerKind.League)

        assertThrows(IllegalArgumentException::class.java) {
            TournamentBusinessRules.validateSeasonRegistration(PlayerKind.Guest)
        }
    }

    @Test
    fun `guest cannot replace themselves`() {
        TournamentBusinessRules.validateGuestParticipation(
            guestPlayerId = 10,
            replacingPlayerId = null,
            guestKind = PlayerKind.Guest,
        )

        assertThrows(IllegalArgumentException::class.java) {
            TournamentBusinessRules.validateGuestParticipation(
                guestPlayerId = 10,
                replacingPlayerId = 10,
                guestKind = PlayerKind.Guest,
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            TournamentBusinessRules.validateGuestParticipation(
                guestPlayerId = 10,
                replacingPlayerId = null,
                guestKind = PlayerKind.League,
            )
        }
    }

    @Test
    fun `season first-place tie pays ninety percent to leaders and ten percent to third`() {
        val standings = listOf(
            standing(playerId = 1, points = 30, rank = 1),
            standing(playerId = 2, points = 30, rank = 1),
            standing(playerId = 3, points = 18, rank = 3),
        )

        val payouts = TournamentBusinessRules.calculateSeasonPayouts(
            payoutEligibleStandings = standings,
            seasonPoolCents = 200_000,
        )

        assertEquals(90_000L, payouts[1])
        assertEquals(90_000L, payouts[2])
        assertEquals(20_000L, payouts[3])
    }

    @Test
    fun `season second-place tie pays first and splits second plus third pool`() {
        val standings = listOf(
            standing(playerId = 1, points = 30, rank = 1),
            standing(playerId = 2, points = 20, rank = 2),
            standing(playerId = 3, points = 20, rank = 2),
        )

        val payouts = TournamentBusinessRules.calculateSeasonPayouts(
            payoutEligibleStandings = standings,
            seasonPoolCents = 200_000,
        )

        assertEquals(120_000L, payouts[1])
        assertEquals(40_000L, payouts[2])
        assertEquals(40_000L, payouts[3])
    }

    @Test
    fun `date two seats ranked players first and leaves zero point players free`() {
        val standings = listOf(
            standing(playerId = 7, points = 20, rank = 1),
            standing(playerId = 8, points = 13, rank = 2),
            standing(playerId = 9, points = 0, rank = 3),
        )

        val seats = TournamentBusinessRules.assignSeats(
            dateNumber = 2,
            playerIds = listOf(9, 8, 7),
            standings = standings,
            randomizeFirstDate = { it },
        )

        assertEquals(1, seats[7])
        assertEquals(2, seats[8])
        assertEquals(null, seats[9])
    }

    private fun playerState(
        playerId: Long,
        rebuyCount: Int = 0,
        buyInCount: Int = 1,
        isBountyPlayer: Boolean = false,
    ): NightPlayerState =
        NightPlayerState(
            id = playerId,
            nightId = 1,
            playerId = playerId,
            status = NightPlayerStatus.Active,
            isBountyPlayer = isBountyPlayer,
            buyInCount = buyInCount,
            rebuyCount = rebuyCount,
        )

    private fun standing(
        playerId: Long,
        points: Int,
        rank: Int,
        isGuest: Boolean = false,
    ): SeasonStanding =
        SeasonStanding(
            id = playerId,
            seasonId = 1,
            playerId = playerId,
            points = points,
            seasonRank = rank,
            playerKind = if (isGuest) PlayerKind.Guest else PlayerKind.League,
            isPayoutEligible = !isGuest,
        )

    private fun result(
        playerId: Long,
        place: Int,
        isGuest: Boolean = false,
    ): NightResult =
        NightResult(
            id = playerId,
            nightId = 1,
            playerId = playerId,
            place = place,
            playerKind = if (isGuest) PlayerKind.Guest else PlayerKind.League,
            isPayoutEligible = !isGuest,
        )
}
