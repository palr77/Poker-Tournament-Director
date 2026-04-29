package com.poker.tournamentdirector.core.database

import androidx.room.TypeConverter
import com.poker.tournamentdirector.core.database.model.ActionType
import com.poker.tournamentdirector.core.database.model.ClockStatus
import com.poker.tournamentdirector.core.database.model.EliminationReason
import com.poker.tournamentdirector.core.database.model.ExportType
import com.poker.tournamentdirector.core.database.model.NightPlayerStatus
import com.poker.tournamentdirector.core.database.model.NightStatus
import com.poker.tournamentdirector.core.database.model.PlayerKind
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun instantToEpochMillis(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun epochMillisToInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun localDateToIso(value: LocalDate?): String? = value?.toString()

    @TypeConverter
    fun isoToLocalDate(value: String?): LocalDate? = value?.let(LocalDate::parse)

    @TypeConverter
    fun bigDecimalToCents(value: BigDecimal?): Long? =
        value?.movePointRight(2)?.setScale(0, RoundingMode.HALF_UP)?.longValueExact()

    @TypeConverter
    fun centsToBigDecimal(value: Long?): BigDecimal? =
        value?.let { BigDecimal.valueOf(it, 2) }

    @TypeConverter
    fun nightStatusToString(value: NightStatus?): String? = value?.name

    @TypeConverter
    fun stringToNightStatus(value: String?): NightStatus? = value?.let(NightStatus::valueOf)

    @TypeConverter
    fun playerKindToString(value: PlayerKind?): String? = value?.name

    @TypeConverter
    fun stringToPlayerKind(value: String?): PlayerKind? = value?.let(PlayerKind::valueOf)

    @TypeConverter
    fun nightPlayerStatusToString(value: NightPlayerStatus?): String? = value?.name

    @TypeConverter
    fun stringToNightPlayerStatus(value: String?): NightPlayerStatus? =
        value?.let(NightPlayerStatus::valueOf)

    @TypeConverter
    fun actionTypeToString(value: ActionType?): String? = value?.name

    @TypeConverter
    fun stringToActionType(value: String?): ActionType? = value?.let(ActionType::valueOf)

    @TypeConverter
    fun eliminationReasonToString(value: EliminationReason?): String? = value?.name

    @TypeConverter
    fun stringToEliminationReason(value: String?): EliminationReason? =
        value?.let(EliminationReason::valueOf)

    @TypeConverter
    fun clockStatusToString(value: ClockStatus?): String? = value?.name

    @TypeConverter
    fun stringToClockStatus(value: String?): ClockStatus? = value?.let(ClockStatus::valueOf)

    @TypeConverter
    fun exportTypeToString(value: ExportType?): String? = value?.name

    @TypeConverter
    fun stringToExportType(value: String?): ExportType? = value?.let(ExportType::valueOf)
}
