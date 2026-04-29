package com.poker.tournamentdirector.data.mapper

import com.poker.tournamentdirector.core.database.entity.BlindLevelEntity
import com.poker.tournamentdirector.core.database.entity.BountyEventEntity
import com.poker.tournamentdirector.core.database.entity.BountyLedgerEntity
import com.poker.tournamentdirector.core.database.entity.ClockStateEntity
import com.poker.tournamentdirector.core.database.entity.ExportSnapshotEntity
import com.poker.tournamentdirector.core.database.entity.GuestParticipationEntity
import com.poker.tournamentdirector.core.database.entity.NightActionEventEntity
import com.poker.tournamentdirector.core.database.entity.NightEntity
import com.poker.tournamentdirector.core.database.entity.NightPlayerStateEntity
import com.poker.tournamentdirector.core.database.entity.NightResultEntity
import com.poker.tournamentdirector.core.database.entity.PlayerEntity
import com.poker.tournamentdirector.core.database.entity.SeasonDateEntity
import com.poker.tournamentdirector.core.database.entity.SeasonEntity
import com.poker.tournamentdirector.core.database.entity.SeasonPlayerRegistrationEntity
import com.poker.tournamentdirector.core.database.entity.SeasonStandingEntity
import com.poker.tournamentdirector.domain.model.BlindLevel
import com.poker.tournamentdirector.domain.model.BountyEvent
import com.poker.tournamentdirector.domain.model.BountyLedger
import com.poker.tournamentdirector.domain.model.ClockState
import com.poker.tournamentdirector.domain.model.ExportSnapshot
import com.poker.tournamentdirector.domain.model.GuestParticipation
import com.poker.tournamentdirector.domain.model.Night
import com.poker.tournamentdirector.domain.model.NightActionEvent
import com.poker.tournamentdirector.domain.model.NightPlayerState
import com.poker.tournamentdirector.domain.model.NightResult
import com.poker.tournamentdirector.domain.model.Player
import com.poker.tournamentdirector.domain.model.Season
import com.poker.tournamentdirector.domain.model.SeasonDate
import com.poker.tournamentdirector.domain.model.SeasonPlayerRegistration
import com.poker.tournamentdirector.domain.model.SeasonStanding

fun SeasonEntity.toDomain(): Season =
    Season(
        id = id,
        name = name,
        startDate = startDate,
        inscriptionFeeCents = inscriptionFeeCents,
        buyInCents = buyInCents,
        rebuyCents = rebuyCents,
        addOnCents = addOnCents,
        bountyCents = bountyCents,
        scheduledDateCount = scheduledDateCount,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun Season.toEntity(): SeasonEntity =
    SeasonEntity(
        id = id,
        name = name,
        startDate = startDate,
        inscriptionFeeCents = inscriptionFeeCents,
        buyInCents = buyInCents,
        rebuyCents = rebuyCents,
        addOnCents = addOnCents,
        bountyCents = bountyCents,
        scheduledDateCount = scheduledDateCount,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

fun SeasonDateEntity.toDomain(): SeasonDate =
    SeasonDate(
        id = id,
        seasonId = seasonId,
        date = date,
        displayOrder = displayOrder,
        hostPlayerId = hostPlayerId,
    )

fun SeasonDate.toEntity(): SeasonDateEntity =
    SeasonDateEntity(
        id = id,
        seasonId = seasonId,
        date = date,
        displayOrder = displayOrder,
        hostPlayerId = hostPlayerId,
    )

fun PlayerEntity.toDomain(): Player =
    Player(
        id = id,
        displayName = displayName,
        kind = kind,
        isArchived = isArchived,
        createdAt = createdAt,
    )

fun Player.toEntity(): PlayerEntity =
    PlayerEntity(
        id = id,
        displayName = displayName,
        kind = kind,
        isArchived = isArchived,
        createdAt = createdAt,
    )

fun SeasonPlayerRegistrationEntity.toDomain(): SeasonPlayerRegistration =
    SeasonPlayerRegistration(
        id = id,
        seasonId = seasonId,
        playerId = playerId,
        registrationNumber = registrationNumber,
        inscriptionPaidCents = inscriptionPaidCents,
        isPayoutEligible = isPayoutEligible,
        joinedAt = joinedAt,
    )

fun SeasonPlayerRegistration.toEntity(): SeasonPlayerRegistrationEntity =
    SeasonPlayerRegistrationEntity(
        id = id,
        seasonId = seasonId,
        playerId = playerId,
        registrationNumber = registrationNumber,
        inscriptionPaidCents = inscriptionPaidCents,
        isPayoutEligible = isPayoutEligible,
        joinedAt = joinedAt,
    )

fun NightEntity.toDomain(): Night =
    Night(
        id = id,
        seasonId = seasonId,
        seasonDateId = seasonDateId,
        nightNumber = nightNumber,
        scheduledDate = scheduledDate,
        status = status,
        startedAt = startedAt,
        closedAt = closedAt,
    )

fun Night.toEntity(): NightEntity =
    NightEntity(
        id = id,
        seasonId = seasonId,
        seasonDateId = seasonDateId,
        nightNumber = nightNumber,
        scheduledDate = scheduledDate,
        status = status,
        startedAt = startedAt,
        closedAt = closedAt,
    )

fun NightPlayerStateEntity.toDomain(): NightPlayerState =
    NightPlayerState(
        id = id,
        nightId = nightId,
        playerId = playerId,
        status = status,
        seatNumber = seatNumber,
        isBountyPlayer = isBountyPlayer,
        buyInCount = buyInCount,
        rebuyCount = rebuyCount,
        hasAddOn = hasAddOn,
        isGuest = isGuest,
        isPayoutEligible = isPayoutEligible,
        eliminatedByPlayerId = eliminatedByPlayerId,
        eliminatedAt = eliminatedAt,
        finalPlace = finalPlace,
    )

fun NightPlayerState.toEntity(): NightPlayerStateEntity =
    NightPlayerStateEntity(
        id = id,
        nightId = nightId,
        playerId = playerId,
        status = status,
        seatNumber = seatNumber,
        isBountyPlayer = isBountyPlayer,
        buyInCount = buyInCount,
        rebuyCount = rebuyCount,
        hasAddOn = hasAddOn,
        isGuest = isGuest,
        isPayoutEligible = isPayoutEligible,
        eliminatedByPlayerId = eliminatedByPlayerId,
        eliminatedAt = eliminatedAt,
        finalPlace = finalPlace,
    )

fun NightActionEventEntity.toDomain(): NightActionEvent =
    NightActionEvent(
        id = id,
        nightId = nightId,
        playerId = playerId,
        targetPlayerId = targetPlayerId,
        action = action,
        amountCents = amountCents,
        isBountySegment = isBountySegment,
        eliminationReason = eliminationReason,
        metadataJson = metadataJson,
        createdAt = createdAt,
    )

fun NightActionEvent.toEntity(): NightActionEventEntity =
    NightActionEventEntity(
        id = id,
        nightId = nightId,
        playerId = playerId,
        targetPlayerId = targetPlayerId,
        action = action,
        amountCents = amountCents,
        isBountySegment = isBountySegment,
        eliminationReason = eliminationReason,
        metadataJson = metadataJson,
        createdAt = createdAt,
    )

fun BlindLevelEntity.toDomain(): BlindLevel =
    BlindLevel(
        id = id,
        seasonId = seasonId,
        levelNumber = levelNumber,
        smallBlind = smallBlind,
        bigBlind = bigBlind,
        ante = ante,
        durationSeconds = durationSeconds,
        isBreak = isBreak,
    )

fun BlindLevel.toEntity(): BlindLevelEntity =
    BlindLevelEntity(
        id = id,
        seasonId = seasonId,
        levelNumber = levelNumber,
        smallBlind = smallBlind,
        bigBlind = bigBlind,
        ante = ante,
        durationSeconds = durationSeconds,
        isBreak = isBreak,
    )

fun ClockStateEntity.toDomain(): ClockState =
    ClockState(
        nightId = nightId,
        currentLevelNumber = currentLevelNumber,
        remainingSeconds = remainingSeconds,
        status = status,
        updatedAt = updatedAt,
    )

fun ClockState.toEntity(): ClockStateEntity =
    ClockStateEntity(
        nightId = nightId,
        currentLevelNumber = currentLevelNumber,
        remainingSeconds = remainingSeconds,
        status = status,
        updatedAt = updatedAt,
    )

fun NightResultEntity.toDomain(): NightResult =
    NightResult(
        id = id,
        nightId = nightId,
        playerId = playerId,
        place = place,
        points = points,
        payoutCents = payoutCents,
        bountyPayoutCents = bountyPayoutCents,
        playerKind = playerKind,
        isPayoutEligible = isPayoutEligible,
    )

fun NightResult.toEntity(): NightResultEntity =
    NightResultEntity(
        id = id,
        nightId = nightId,
        playerId = playerId,
        place = place,
        points = points,
        payoutCents = payoutCents,
        bountyPayoutCents = bountyPayoutCents,
        playerKind = playerKind,
        isPayoutEligible = isPayoutEligible,
    )

fun SeasonStandingEntity.toDomain(): SeasonStanding =
    SeasonStanding(
        id = id,
        seasonId = seasonId,
        playerId = playerId,
        points = points,
        nightsPlayed = nightsPlayed,
        wins = wins,
        topFourFinishes = topFourFinishes,
        knockoutsFor = knockoutsFor,
        knockoutsAgainst = knockoutsAgainst,
        rebuys = rebuys,
        addOns = addOns,
        seasonRank = seasonRank,
        seasonPayoutCents = seasonPayoutCents,
        playerKind = playerKind,
        isPayoutEligible = isPayoutEligible,
    )

fun SeasonStanding.toEntity(): SeasonStandingEntity =
    SeasonStandingEntity(
        id = id,
        seasonId = seasonId,
        playerId = playerId,
        points = points,
        nightsPlayed = nightsPlayed,
        wins = wins,
        topFourFinishes = topFourFinishes,
        knockoutsFor = knockoutsFor,
        knockoutsAgainst = knockoutsAgainst,
        rebuys = rebuys,
        addOns = addOns,
        seasonRank = seasonRank,
        seasonPayoutCents = seasonPayoutCents,
        playerKind = playerKind,
        isPayoutEligible = isPayoutEligible,
    )

fun BountyEventEntity.toDomain(): BountyEvent =
    BountyEvent(
        id = id,
        nightId = nightId,
        bountyPlayerId = bountyPlayerId,
        collectedByPlayerId = collectedByPlayerId,
        amountCents = amountCents,
        createdAt = createdAt,
    )

fun BountyEvent.toEntity(): BountyEventEntity =
    BountyEventEntity(
        id = id,
        nightId = nightId,
        bountyPlayerId = bountyPlayerId,
        collectedByPlayerId = collectedByPlayerId,
        amountCents = amountCents,
        createdAt = createdAt,
    )

fun BountyLedgerEntity.toDomain(): BountyLedger =
    BountyLedger(
        id = id,
        seasonId = seasonId,
        playerId = playerId,
        bountiesWon = bountiesWon,
        bountiesLost = bountiesLost,
        amountWonCents = amountWonCents,
        amountLostCents = amountLostCents,
    )

fun BountyLedger.toEntity(): BountyLedgerEntity =
    BountyLedgerEntity(
        id = id,
        seasonId = seasonId,
        playerId = playerId,
        bountiesWon = bountiesWon,
        bountiesLost = bountiesLost,
        amountWonCents = amountWonCents,
        amountLostCents = amountLostCents,
    )

fun GuestParticipationEntity.toDomain(): GuestParticipation =
    GuestParticipation(
        id = id,
        nightId = nightId,
        guestPlayerId = guestPlayerId,
        replacingPlayerId = replacingPlayerId,
        buyInCents = buyInCents,
        rebuysCents = rebuysCents,
        addOnCents = addOnCents,
        pointsEarned = pointsEarned,
        place = place,
        createdAt = createdAt,
    )

fun GuestParticipation.toEntity(): GuestParticipationEntity =
    GuestParticipationEntity(
        id = id,
        nightId = nightId,
        guestPlayerId = guestPlayerId,
        replacingPlayerId = replacingPlayerId,
        buyInCents = buyInCents,
        rebuysCents = rebuysCents,
        addOnCents = addOnCents,
        pointsEarned = pointsEarned,
        place = place,
        createdAt = createdAt,
    )

fun ExportSnapshotEntity.toDomain(): ExportSnapshot =
    ExportSnapshot(
        id = id,
        seasonId = seasonId,
        nightId = nightId,
        exportType = exportType,
        payload = payload,
        createdAt = createdAt,
    )

fun ExportSnapshot.toEntity(): ExportSnapshotEntity =
    ExportSnapshotEntity(
        id = id,
        seasonId = seasonId,
        nightId = nightId,
        exportType = exportType,
        payload = payload,
        createdAt = createdAt,
    )
