package com.poker.tournamentdirector.data.repository

import com.poker.tournamentdirector.core.database.dao.SeasonDao
import com.poker.tournamentdirector.core.database.entity.SeasonEntity
import com.poker.tournamentdirector.domain.model.SeasonSummary
import com.poker.tournamentdirector.domain.repository.SeasonRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomSeasonRepository @Inject constructor(
    private val seasonDao: SeasonDao,
) : SeasonRepository {
    override fun observeSeasons(): Flow<List<SeasonSummary>> =
        seasonDao.observeSeasons().map { seasons -> seasons.map(SeasonEntity::toSummary) }

    override fun observeActiveSeason(): Flow<SeasonSummary?> =
        seasonDao.observeActiveSeason().map { season -> season?.toSummary() }
}

private fun SeasonEntity.toSummary(): SeasonSummary =
    SeasonSummary(
        id = id,
        name = name,
        isActive = isActive,
        buyInCents = buyInCents,
    )
