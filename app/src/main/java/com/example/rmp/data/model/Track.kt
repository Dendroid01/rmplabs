package com.example.rmp.data.model

data class Track(
    val id: Int,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val coverColor: Long,
    var isLiked: Boolean = false
)