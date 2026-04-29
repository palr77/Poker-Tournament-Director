package com.poker.tournamentdirector.data.repository

import com.poker.tournamentdirector.core.database.dao.NightDao
import com.poker.tournamentdirector.core.database.entity.NightEntity
import com.poker.tournamentdirector.domain.model.NightSummary
import com.poker.tournamentdirector.domain.repository.NightRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomNightRepository @Inject constructor(
    private val nightDao: NightDao,
) : NightRepository {
    override fun observeNightsForSeason(seasonId: Long): Flow<List<NightSummary>> =
        nightDao.observeNightsForSeason(seasonId).map { nights -> nights.map(NightEntity::toSummary) }
}

private fun NightEntity.toSummary(): NightSummary =
    NightSummary(
        id = id,
        seasonId = seasonId,
        nightNumber = nightNumber,
        statusLabel = status.name,
    )
