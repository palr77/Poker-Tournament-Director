package com.poker.tournamentdirector.data.repository

import com.poker.tournamentdirector.core.database.dao.ExportDao
import com.poker.tournamentdirector.data.mapper.toDomain
import com.poker.tournamentdirector.data.mapper.toEntity
import com.poker.tournamentdirector.domain.model.ExportSnapshot
import com.poker.tournamentdirector.domain.model.ExportType
import com.poker.tournamentdirector.domain.repository.ExportRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomExportRepository @Inject constructor(
    private val exportDao: ExportDao,
) : ExportRepository {
    override suspend fun saveExportSnapshot(snapshot: ExportSnapshot): Long =
        exportDao.insertExportSnapshot(snapshot.copy(id = 0).toEntity())

    override fun observeSeasonExports(seasonId: Long): Flow<List<ExportSnapshot>> =
        exportDao.observeSeasonExportSnapshots(seasonId).map { snapshots ->
            snapshots.map { it.toDomain() }
        }

    override fun observeNightExports(nightId: Long): Flow<List<ExportSnapshot>> =
        exportDao.observeNightExportSnapshots(nightId).map { snapshots ->
            snapshots.map { it.toDomain() }
        }

    override suspend fun getSeasonExports(
        seasonId: Long,
        exportType: ExportType,
    ): List<ExportSnapshot> =
        exportDao.getSeasonExportSnapshots(seasonId, exportType).map { it.toDomain() }

    override suspend fun getNightExports(
        nightId: Long,
        exportType: ExportType,
    ): List<ExportSnapshot> =
        exportDao.getNightExportSnapshots(nightId, exportType).map { it.toDomain() }
}
