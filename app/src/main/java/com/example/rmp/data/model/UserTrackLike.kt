package com.example.rmp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "user_track_likes",
    primaryKeys = ["userId", "trackId"],
    indices = [Index("userId")]
)
data class UserTrackLike(
    val userId: Int,
    val trackId: String
)