package com.example.rmp.data.model

data class Playlist(
    val id: Int,
    val title: String,
    val trackCount: Int,
    val coverColor: Long,
    val isAlbum: Boolean = false,
    val artist: String = ""
)