package com.poker.tournamentdirector.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.poker.tournamentdirector.core.database.entity.ExportSnapshotEntity
import com.poker.tournamentdirector.domain.model.ExportType
import kotlinx.coroutines.flow.Flow

@Dao
interface ExportDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExportSnapshot(snapshot: ExportSnapshotEntity): Long

    @Query(
        """
        SELECT * FROM export_snapshots
        WHERE season_id = :seasonId
        ORDER BY created_at DESC, id DESC
        """,
    )
    fun observeSeasonExportSnapshots(seasonId: Long): Flow<List<ExportSnapshotEntity>>

    @Query(
        """
        SELECT * FROM export_snapshots
        WHERE night_id = :nightId
        ORDER BY created_at DESC, id DESC
        """,
    )
    fun observeNightExportSnapshots(nightId: Long): Flow<List<ExportSnapshotEntity>>

    @Query(
        """
        SELECT * FROM export_snapshots
        WHERE season_id = :seasonId AND export_type = :exportType
        ORDER BY created_at DESC, id DESC
        """,
    )
    suspend fun getSeasonExportSnapshots(
        seasonId: Long,
        exportType: ExportType,
    ): List<ExportSnapshotEntity>

    @Query(
        """
        SELECT * FROM export_snapshots
        WHERE night_id = :nightId AND export_type = :exportType
        ORDER BY created_at DESC, id DESC
        """,
    )
    suspend fun getNightExportSnapshots(
        nightId: Long,
        exportType: ExportType,
    ): List<ExportSnapshotEntity>
}
