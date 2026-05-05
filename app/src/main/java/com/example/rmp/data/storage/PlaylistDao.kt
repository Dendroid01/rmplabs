package com.example.rmp.data.storage

import androidx.room.*
import com.example.rmp.data.model.PlaylistTrack
import com.example.rmp.data.model.UserPlaylist
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    // --- Плейлисты ---
    @Query("SELECT * FROM user_playlists WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPlaylistsForUser(userId: Int): Flow<List<UserPlaylist>>

    @Insert
    suspend fun createPlaylist(playlist: UserPlaylist): Long

    @Delete
    suspend fun deletePlaylist(playlist: UserPlaylist)

    @Query("UPDATE user_playlists SET name = :name WHERE id = :id")
    suspend fun renamePlaylist(id: Int, name: String)

    // --- Треки в плейлисте ---
    @Query("SELECT trackId FROM playlist_tracks WHERE playlistId = :playlistId ORDER BY addedAt ASC")
    fun getTrackIdsForPlaylist(playlistId: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTrackToPlaylist(entry: PlaylistTrack)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrackFromPlaylist(playlistId: Int, trackId: String)

    @Query("SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = :playlistId")
    fun getTrackCount(playlistId: Int): Flow<Int>
}