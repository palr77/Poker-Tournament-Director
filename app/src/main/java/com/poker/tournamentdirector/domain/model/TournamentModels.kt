package com.poker.tournamentdirector.domain.model

import java.time.Instant
import java.time.LocalDate

enum class NightStatus {
    Scheduled,
    Active,
    Closed,
}

enum class PlayerKind {
    League,
    Guest,
}

enum class NightPlayerStatus {
    Registered,
    Active,
    Eliminated,
    Winner,
}

enum class ActionType {
    BuyIn,
    Rebuy,
    AddOn,
    Elimination,
    Bounty,
    SeatAssignment,
    ClockAdjustment,
}

enum class EliminationReason {
    Standard,
    Bounty,
    NoShow,
    AdminCorrection,
}

enum class ClockStatus {
    Ready,
    Running,
    Paused,
    Finished,
}

enum class ExportType {
    Csv,
    WhatsAppText,
}

data class Season(
    val id: Long = 0,
    val name: String,
    val startDate: LocalDate? = null,
    val inscriptionFeeCents: Long = 200_000,
    val buyInCents: Long = 20_000,
    val rebuyCents: Long = 20_000,
    val addOnCents: Long = 20_000,
    val bountyCents: Long = 0,
    val scheduledDateCount: Int = 10,
    val isActive: Boolean = true,
    val createdAt: Instant = Instant.now(),
    val updatedAt: Instant = createdAt,
)

data class SeasonDate(
    val id: Long = 0,
    val seasonId: Long,
    val date: LocalDate,
    val displayOrder: Int,
    val hostPlayerId: Long? = null,
)

data class Player(
    val id: Long = 0,
    val displayName: String,
    val kind: PlayerKind = PlayerKind.League,
    val isArchived: Boolean = false,
    val createdAt: Instant = Instant.now(),
)

data class SeasonPlayerRegistration(
    val id: Long = 0,
    val seasonId: Long,
    val playerId: Long,
    val registrationNumber: Int = 0,
    val inscriptionPaidCents: Long = 0,
    val isPayoutEligible: Boolean = true,
    val joinedAt: Instant = Instant.now(),
)

data class Night(
    val id: Long = 0,
    val seasonId: Long,
    val seasonDateId: Long? = null,
    val nightNumber: Int,
    val scheduledDate: LocalDate? = null,
    val status: NightStatus = NightStatus.Scheduled,
    val startedAt: Instant? = null,
    val closedAt: Instant? = null,
)

data class NightPlayerState(
    val id: Long = 0,
    val nightId: Long,
    val playerId: Long,
    val status: NightPlayerStatus = NightPlayerStatus.Registered,
    val seatNumber: Int? = null,
    val isBountyPlayer: Boolean = false,
    val buyInCount: Int = 0,
    val rebuyCount: Int = 0,
    val hasAddOn: Boolean = false,
    val isGuest: Boolean = false,
    val isPayoutEligible: Boolean = true,
    val eliminatedByPlayerId: Long? = null,
    val eliminatedAt: Instant? = null,
    val finalPlace: Int? = null,
)

data class NightActionEvent(
    val id: Long = 0,
    val nightId: Long,
    val playerId: Long,
    val targetPlayerId: Long? = null,
    val action: ActionType,
    val amountCents: Long = 0,
    val isBountySegment: Boolean = false,
    val eliminationReason: EliminationReason? = null,
    val metadataJson: String? = null,
    val createdAt: Instant = Instant.now(),
)

data class BlindLevel(
    val id: Long = 0,
    val seasonId: Long,
    val levelNumber: Int,
    val smallBlind: Int,
    val bigBlind: Int,
    val ante: Int = 0,
    val durationSeconds: Int,
    val isBreak: Boolean = false,
)

data class ClockState(
    val nightId: Long,
    val currentLevelNumber: Int = 1,
    val remainingSeconds: Int = 0,
    val status: ClockStatus = ClockStatus.Ready,
    val updatedAt: Instant = Instant.now(),
)

data class NightResult(
    val id: Long = 0,
    val nightId: Long,
    val playerId: Long,
    val place: Int,
    val points: Int = 0,
    val payoutCents: Long = 0,
    val bountyPayoutCents: Long = 0,
    val playerKind: PlayerKind = PlayerKind.League,
    val isPayoutEligible: Boolean = true,
)

data class SeasonStanding(
    val id: Long = 0,
    val seasonId: Long,
    val playerId: Long,
    val points: Int = 0,
    val nightsPlayed: Int = 0,
    val wins: Int = 0,
    val topFourFinishes: Int = 0,
    val knockoutsFor: Int = 0,
    val knockoutsAgainst: Int = 0,
    val rebuys: Int = 0,
    val addOns: Int = 0,
    val seasonRank: Int = 0,
    val seasonPayoutCents: Long = 0,
    val playerKind: PlayerKind = PlayerKind.League,
    val isPayoutEligible: Boolean = true,
)

data class BountyEvent(
    val id: Long = 0,
    val nightId: Long,
    val bountyPlayerId: Long,
    val collectedByPlayerId: Long,
    val amountCents: Long = 0,
    val createdAt: Instant = Instant.now(),
)

data class BountyLedger(
    val id: Long = 0,
    val seasonId: Long,
    val playerId: Long,
    val bountiesWon: Int = 0,
    val bountiesLost: Int = 0,
    val amountWonCents: Long = 0,
    val amountLostCents: Long = 0,
)

data class GuestParticipation(
    val id: Long = 0,
    val nightId: Long,
    val guestPlayerId: Long,
    val replacingPlayerId: Long? = null,
    val buyInCents: Long = 0,
    val rebuysCents: Long = 0,
    val addOnCents: Long = 0,
    val pointsEarned: Int = 0,
    val place: Int? = null,
    val createdAt: Instant = Instant.now(),
)

data class ExportSnapshot(
    val id: Long = 0,
    val seasonId: Long,
    val nightId: Long? = null,
    val exportType: ExportType,
    val payload: String,
    val createdAt: Instant = Instant.now(),
)
