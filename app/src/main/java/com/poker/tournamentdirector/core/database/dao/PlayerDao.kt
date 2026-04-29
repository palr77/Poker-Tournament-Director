package com.poker.tournamentdirector.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.poker.tournamentdirector.core.database.entity.PlayerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE is_archived = 0 ORDER BY display_name COLLATE NOCASE ASC")
    fun observeActivePlayers(): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayer(playerId: Long): PlayerEntity?

    @Query(
        """
        SELECT * FROM players
        WHERE is_archived = 0 AND display_name LIKE '%' || :query || '%'
        ORDER BY display_name COLLATE NOCASE ASC
        """,
    )
    fun searchPlayers(query: String): Flow<List<PlayerEntity>>

    @Upsert
    suspend fun upsertPlayer(player: PlayerEntity)

    @Query("UPDATE players SET is_archived = 1 WHERE id = :playerId")
    suspend fun archivePlayer(playerId: Long)
}
