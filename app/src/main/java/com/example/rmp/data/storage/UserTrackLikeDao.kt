package com.example.rmp.data.storage

import androidx.room.*
import com.example.rmp.data.model.UserTrackLike
import kotlinx.coroutines.flow.Flow

@Dao
interface UserTrackLikeDao {
    @Query("SELECT trackId FROM user_track_likes WHERE userId = :userId")
    fun getLikedTrackIds(userId: Int): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun like(like: UserTrackLike)

    @Delete
    suspend fun unlike(like: UserTrackLike)

    @Query("SELECT EXISTS(SELECT 1 FROM user_track_likes WHERE userId=:userId AND trackId=:trackId)")
    suspend fun isLiked(userId: Int, trackId: String): Boolean
}