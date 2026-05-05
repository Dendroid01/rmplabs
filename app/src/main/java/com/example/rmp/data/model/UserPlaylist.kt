package com.example.rmp.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_playlists",
    indices = [Index("userId")]
)
data class UserPlaylist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val name: String,
    val createdAt: Long = System.currentTimeMillis()
)