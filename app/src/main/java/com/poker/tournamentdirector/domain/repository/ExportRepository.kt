package com.poker.tournamentdirector.domain.repository

import com.poker.tournamentdirector.domain.model.ExportSnapshot
import com.poker.tournamentdirector.domain.model.ExportType
import kotlinx.coroutines.flow.Flow

interface ExportRepository {
    suspend fun saveExportSnapshot(snapshot: ExportSnapshot): Long

    fun observeSeasonExports(seasonId: Long): Flow<List<ExportSnapshot>>

    fun observeNightExports(nightId: Long): Flow<List<ExportSnapshot>>

    suspend fun getSeasonExports(
        seasonId: Long,
        exportType: ExportType,
    ): List<ExportSnapshot>

    suspend fun getNightExports(
        nightId: Long,
        exportType: ExportType,
    ): List<ExportSnapshot>
}
