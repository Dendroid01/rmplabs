package com.example.rmp.data.model

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "playlist_tracks",
    primaryKeys = ["playlistId", "trackId"],
    indices = [Index("playlistId")]
)
data class PlaylistTrack(
    val playlistId: Int,
    val trackId: String,
    val addedAt: Long = System.currentTimeMillis()
)