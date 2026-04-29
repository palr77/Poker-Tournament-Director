package com.poker.tournamentdirector.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.poker.tournamentdirector.core.database.model.ActionType
import com.poker.tournamentdirector.core.database.model.ClockStatus
import com.poker.tournamentdirector.core.database.model.EliminationReason
import com.poker.tournamentdirector.core.database.model.ExportType
import com.poker.tournamentdirector.core.database.model.NightPlayerStatus
import com.poker.tournamentdirector.core.database.model.NightStatus
import com.poker.tournamentdirector.core.database.model.PlayerKind
import java.time.Instant
import java.time.LocalDate

@Entity(tableName = "seasons")
data class SeasonEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    @ColumnInfo(name = "start_date") val startDate: LocalDate? = null,
    @ColumnInfo(name = "buy_in_cents") val buyInCents: Long = 0,
    @ColumnInfo(name = "rebuy_cents") val rebuyCents: Long = 0,
    @ColumnInfo(name = "add_on_cents") val addOnCents: Long = 0,
    @ColumnInfo(name = "bounty_cents") val bountyCents: Long = 0,
    @ColumnInfo(name = "is_active") val isActive: Boolean = true,
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
)

@Entity(
    tableName = "season_dates",
    foreignKeys = [
        ForeignKey(
            entity = SeasonEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("season_id"), Index(value = ["season_id", "date"], unique = true)],
)
data class SeasonDateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "season_id") val seasonId: Long,
    val date: LocalDate,
    @ColumnInfo(name = "display_order") val displayOrder: Int,
)

@Entity(
    tableName = "players",
    indices = [Index(value = ["display_name"], unique = true)],
)
data class PlayerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "display_name") val displayName: String,
    val kind: PlayerKind = PlayerKind.League,
    @ColumnInfo(name = "is_archived") val isArchived: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
)

@Entity(
    tableName = "season_player_registrations",
    foreignKeys = [
        ForeignKey(
            entity = SeasonEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("season_id"),
        Index("player_id"),
        Index(value = ["season_id", "player_id"], unique = true),
    ],
)
data class SeasonPlayerRegistrationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "season_id") val seasonId: Long,
    @ColumnInfo(name = "player_id") val playerId: Long,
    @ColumnInfo(name = "joined_at") val joinedAt: Instant = Instant.now(),
)

@Entity(
    tableName = "nights",
    foreignKeys = [
        ForeignKey(
            entity = SeasonEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = SeasonDateEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_date_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("season_id"), Index("season_date_id")],
)
data class NightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "season_id") val seasonId: Long,
    @ColumnInfo(name = "season_date_id") val seasonDateId: Long? = null,
    val status: NightStatus = NightStatus.Scheduled,
    @ColumnInfo(name = "night_number") val nightNumber: Int,
    @ColumnInfo(name = "started_at") val startedAt: Instant? = null,
    @ColumnInfo(name = "closed_at") val closedAt: Instant? = null,
)

@Entity(
    tableName = "night_player_states",
    foreignKeys = [
        ForeignKey(
            entity = NightEntity::class,
            parentColumns = ["id"],
            childColumns = ["night_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("night_id"),
        Index("player_id"),
        Index(value = ["night_id", "player_id"], unique = true),
    ],
)
data class NightPlayerStateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "night_id") val nightId: Long,
    @ColumnInfo(name = "player_id") val playerId: Long,
    val status: NightPlayerStatus = NightPlayerStatus.Registered,
    @ColumnInfo(name = "seat_number") val seatNumber: Int? = null,
    @ColumnInfo(name = "rebuy_count") val rebuyCount: Int = 0,
    @ColumnInfo(name = "has_add_on") val hasAddOn: Boolean = false,
    @ColumnInfo(name = "eliminated_by_player_id") val eliminatedByPlayerId: Long? = null,
    @ColumnInfo(name = "eliminated_at") val eliminatedAt: Instant? = null,
)

@Entity(
    tableName = "night_action_events",
    foreignKeys = [
        ForeignKey(
            entity = NightEntity::class,
            parentColumns = ["id"],
            childColumns = ["night_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["target_player_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("night_id"), Index("player_id"), Index("target_player_id")],
)
data class NightActionEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "night_id") val nightId: Long,
    @ColumnInfo(name = "player_id") val playerId: Long,
    @ColumnInfo(name = "target_player_id") val targetPlayerId: Long? = null,
    val action: ActionType,
    @ColumnInfo(name = "amount_cents") val amountCents: Long = 0,
    @ColumnInfo(name = "elimination_reason") val eliminationReason: EliminationReason? = null,
    @ColumnInfo(name = "metadata_json") val metadataJson: String? = null,
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
)

@Entity(
    tableName = "blind_levels",
    foreignKeys = [
        ForeignKey(
            entity = SeasonEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("season_id"), Index(value = ["season_id", "level_number"], unique = true)],
)
data class BlindLevelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "season_id") val seasonId: Long,
    @ColumnInfo(name = "level_number") val levelNumber: Int,
    @ColumnInfo(name = "small_blind") val smallBlind: Int,
    @ColumnInfo(name = "big_blind") val bigBlind: Int,
    val ante: Int = 0,
    @ColumnInfo(name = "duration_seconds") val durationSeconds: Int,
    @ColumnInfo(name = "is_break") val isBreak: Boolean = false,
)

@Entity(
    tableName = "clock_states",
    foreignKeys = [
        ForeignKey(
            entity = NightEntity::class,
            parentColumns = ["id"],
            childColumns = ["night_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("night_id")],
)
data class ClockStateEntity(
    @PrimaryKey
    @ColumnInfo(name = "night_id")
    val nightId: Long,
    @ColumnInfo(name = "current_level_number") val currentLevelNumber: Int = 1,
    @ColumnInfo(name = "remaining_seconds") val remainingSeconds: Int = 0,
    val status: ClockStatus = ClockStatus.Ready,
    @ColumnInfo(name = "updated_at") val updatedAt: Instant = Instant.now(),
)

@Entity(
    tableName = "night_results",
    foreignKeys = [
        ForeignKey(
            entity = NightEntity::class,
            parentColumns = ["id"],
            childColumns = ["night_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("night_id"),
        Index("player_id"),
        Index(value = ["night_id", "player_id"], unique = true),
    ],
)
data class NightResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "night_id") val nightId: Long,
    @ColumnInfo(name = "player_id") val playerId: Long,
    val place: Int,
    val points: Int = 0,
    @ColumnInfo(name = "payout_cents") val payoutCents: Long = 0,
    @ColumnInfo(name = "bounty_payout_cents") val bountyPayoutCents: Long = 0,
)

@Entity(
    tableName = "season_standings",
    foreignKeys = [
        ForeignKey(
            entity = SeasonEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("season_id"),
        Index("player_id"),
        Index(value = ["season_id", "player_id"], unique = true),
    ],
)
data class SeasonStandingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "season_id") val seasonId: Long,
    @ColumnInfo(name = "player_id") val playerId: Long,
    val points: Int = 0,
    @ColumnInfo(name = "nights_played") val nightsPlayed: Int = 0,
    @ColumnInfo(name = "season_rank") val seasonRank: Int = 0,
    @ColumnInfo(name = "season_payout_cents") val seasonPayoutCents: Long = 0,
)

@Entity(
    tableName = "bounty_events",
    foreignKeys = [
        ForeignKey(
            entity = NightEntity::class,
            parentColumns = ["id"],
            childColumns = ["night_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["collected_by_player_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("night_id"), Index("player_id"), Index("collected_by_player_id")],
)
data class BountyEventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "night_id") val nightId: Long,
    @ColumnInfo(name = "player_id") val playerId: Long,
    @ColumnInfo(name = "collected_by_player_id") val collectedByPlayerId: Long? = null,
    @ColumnInfo(name = "amount_cents") val amountCents: Long = 0,
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
)

@Entity(
    tableName = "bounty_ledgers",
    foreignKeys = [
        ForeignKey(
            entity = SeasonEntity::class,
            parentColumns = ["id"],
            childColumns = ["season_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("season_id"), Index("player_id")],
)
data class BountyLedgerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "season_id") val seasonId: Long,
    @ColumnInfo(name = "player_id") val playerId: Long,
    @ColumnInfo(name = "bounties_won") val bountiesWon: Int = 0,
    @ColumnInfo(name = "amount_cents") val amountCents: Long = 0,
)

@Entity(
    tableName = "guest_participations",
    foreignKeys = [
        ForeignKey(
            entity = NightEntity::class,
            parentColumns = ["id"],
            childColumns = ["night_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index("night_id"),
        Index("player_id"),
        Index(value = ["night_id", "player_id"], unique = true),
    ],
)
data class GuestParticipationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "night_id") val nightId: Long,
    @ColumnInfo(name = "player_id") val playerId: Long,
    @ColumnInfo(name = "buy_in_cents") val buyInCents: Long = 0,
    @ColumnInfo(name = "rebuys_cents") val rebuysCents: Long = 0,
    @ColumnInfo(name = "add_on_cents") val addOnCents: Long = 0,
)

@Entity(
    tableName = "export_snapshots",
    foreignKeys = [
        ForeignKey(
            entity = NightEntity::class,
            parentColumns = ["id"],
            childColumns = ["night_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("night_id"), Index("export_type")],
)
data class ExportSnapshotEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "night_id") val nightId: Long,
    @ColumnInfo(name = "export_type") val exportType: ExportType,
    val payload: String,
    @ColumnInfo(name = "created_at") val createdAt: Instant = Instant.now(),
)
