package com.poker.tournamentdirector.data.repository

import com.poker.tournamentdirector.core.database.dao.PlayerDao
import com.poker.tournamentdirector.data.mapper.toDomain
import com.poker.tournamentdirector.data.mapper.toEntity
import com.poker.tournamentdirector.domain.model.Player
import com.poker.tournamentdirector.domain.model.PlayerKind
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
        playerDao.observeActivePlayers().map { players -> players.map { it.toDomain().toSummary() } }

    override fun observePlayersByKind(kind: PlayerKind): Flow<List<Player>> =
        playerDao.observePlayersByKind(kind).map { players -> players.map { it.toDomain() } }

    override fun observePlayer(playerId: Long): Flow<Player?> =
        playerDao.observePlayer(playerId).map { player -> player?.toDomain() }

    override suspend fun getPlayer(playerId: Long): Player? =
        playerDao.getPlayer(playerId)?.toDomain()

    override fun searchPlayers(query: String): Flow<List<Player>> =
        playerDao.searchPlayers(query).map { players -> players.map { it.toDomain() } }

    override suspend fun createPlayer(player: Player): Long =
        playerDao.insertPlayer(player.copy(id = 0).toEntity())

    override suspend fun updatePlayer(player: Player) {
        playerDao.updatePlayer(player.toEntity())
    }

    override suspend fun createGuest(displayName: String): Long {
        playerDao.findByDisplayName(displayName)?.let { existing ->
            require(existing.kind == PlayerKind.Guest) {
                "A league player already uses the display name $displayName."
            }
            return existing.id
        }

        return playerDao.insertPlayer(
            Player(
                displayName = displayName,
                kind = PlayerKind.Guest,
            ).toEntity(),
        )
    }

    override suspend fun archivePlayer(playerId: Long) {
        playerDao.archivePlayer(playerId)
    }

    override suspend fun restorePlayer(playerId: Long) {
        playerDao.restorePlayer(playerId)
    }
}

private fun Player.toSummary(): PlayerSummary =
    PlayerSummary(
        id = id,
        displayName = displayName,
        isGuest = kind == PlayerKind.Guest,
    )
