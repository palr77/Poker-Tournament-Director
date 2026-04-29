package com.poker.tournamentdirector.domain.repository

import com.poker.tournamentdirector.domain.model.Player
import com.poker.tournamentdirector.domain.model.PlayerKind
import com.poker.tournamentdirector.domain.model.PlayerSummary
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    fun observeActivePlayers(): Flow<List<PlayerSummary>>

    fun observePlayersByKind(kind: PlayerKind): Flow<List<Player>>

    fun observePlayer(playerId: Long): Flow<Player?>

    suspend fun getPlayer(playerId: Long): Player?

    fun searchPlayers(query: String): Flow<List<Player>>

    suspend fun createPlayer(player: Player): Long

    suspend fun updatePlayer(player: Player)

    suspend fun createGuest(displayName: String): Long

    suspend fun archivePlayer(playerId: Long)

    suspend fun restorePlayer(playerId: Long)
}
