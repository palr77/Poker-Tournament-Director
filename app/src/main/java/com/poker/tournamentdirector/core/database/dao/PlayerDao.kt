package com.poker.tournamentdirector.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.poker.tournamentdirector.core.database.entity.PlayerEntity
import com.poker.tournamentdirector.domain.model.PlayerKind
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Query("SELECT * FROM players WHERE is_archived = 0 ORDER BY display_name COLLATE NOCASE ASC")
    fun observeActivePlayers(): Flow<List<PlayerEntity>>

    @Query(
        """
        SELECT * FROM players
        WHERE is_archived = 0 AND kind = :kind
        ORDER BY display_name COLLATE NOCASE ASC
        """,
    )
    fun observePlayersByKind(kind: PlayerKind): Flow<List<PlayerEntity>>

    @Query("SELECT * FROM players WHERE id = :playerId")
    fun observePlayer(playerId: Long): Flow<PlayerEntity?>

    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayer(playerId: Long): PlayerEntity?

    @Query("SELECT * FROM players WHERE display_name COLLATE NOCASE = :displayName LIMIT 1")
    suspend fun findByDisplayName(displayName: String): PlayerEntity?

    @Query(
        """
        SELECT * FROM players
        WHERE is_archived = 0 AND display_name LIKE '%' || :query || '%'
        ORDER BY display_name COLLATE NOCASE ASC
        """,
    )
    fun searchPlayers(query: String): Flow<List<PlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertPlayer(player: PlayerEntity): Long

    @Update
    suspend fun updatePlayer(player: PlayerEntity)

    @Upsert
    suspend fun upsertPlayer(player: PlayerEntity)

    @Query("UPDATE players SET is_archived = 1 WHERE id = :playerId")
    suspend fun archivePlayer(playerId: Long)

    @Query("UPDATE players SET is_archived = 0 WHERE id = :playerId")
    suspend fun restorePlayer(playerId: Long)
}
