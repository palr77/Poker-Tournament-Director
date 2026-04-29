package com.poker.tournamentdirector.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.poker.tournamentdirector.core.database.dao.BountyDao
import com.poker.tournamentdirector.core.database.dao.ExportDao
import com.poker.tournamentdirector.core.database.dao.NightDao
import com.poker.tournamentdirector.core.database.dao.PlayerDao
import com.poker.tournamentdirector.core.database.dao.SeasonDao
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

@Database(
    entities = [
        SeasonEntity::class,
        SeasonDateEntity::class,
        PlayerEntity::class,
        SeasonPlayerRegistrationEntity::class,
        NightEntity::class,
        NightPlayerStateEntity::class,
        NightActionEventEntity::class,
        BlindLevelEntity::class,
        ClockStateEntity::class,
        NightResultEntity::class,
        SeasonStandingEntity::class,
        BountyEventEntity::class,
        BountyLedgerEntity::class,
        GuestParticipationEntity::class,
        ExportSnapshotEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(TournamentTypeConverters::class)
abstract class TournamentDirectorDatabase : RoomDatabase() {
    abstract fun seasonDao(): SeasonDao
    abstract fun playerDao(): PlayerDao
    abstract fun nightDao(): NightDao
    abstract fun bountyDao(): BountyDao
    abstract fun exportDao(): ExportDao

    companion object {
        const val DATABASE_NAME = "poker_tournament_director.db"
    }
}
