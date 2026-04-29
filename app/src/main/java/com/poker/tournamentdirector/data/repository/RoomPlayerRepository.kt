package com.poker.tournamentdirector.data.repository

import com.poker.tournamentdirector.core.database.dao.PlayerDao
import com.poker.tournamentdirector.core.database.entity.PlayerEntity
import com.poker.tournamentdirector.core.database.model.PlayerKind
import com.poker.tournamentdirector.domain.model.PlayerSummary
import com.poker.tournamentdirector.domain.repository.PlayerRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomPlayerRepository @Inject constructor(
    private val playerDao: PlayerDao,
) : PlayerRepository {
    override fun observeActivePlayers(): Flow<List<PlayerSummary>> =
        playerDao.observeActivePlayers().map { players -> players.map(PlayerEntity::toSummary) }
}

private fun PlayerEntity.toSummary(): PlayerSummary =
    PlayerSummary(
        id = id,
        displayName = displayName,
        isGuest = kind == PlayerKind.Guest,
    )
