package com.example.rmp.data.model

import com.google.gson.annotations.SerializedName

data class DeezerSearchResponse(
    val data: List<DeezerTrack>,
    val total: Int
)

data class DeezerTrack(
    val id: Long,
    val title: String,
    val duration: Int,
    @SerializedName("preview") val previewUrl: String,  // 30-сек превью, всегда бесплатно
    val artist: DeezerArtist,
    val album: DeezerAlbum
)

data class DeezerArtist(
    val id: Long,
    val name: String,
    @SerializedName("picture_medium") val pictureMedium: String = ""
)

data class DeezerAlbum(
    val id: Long,
    val title: String,
    @SerializedName("cover_medium") val coverMedium: String = ""
)

// Унифицированная модель для UI (объединяет Deezer и local)
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val coverUrl: String = "",      // URL обложки (Deezer)
    val coverColor: Long = 0xFF1DB954,  // fallback цвет
    val previewUrl: String = ""     // 30-сек превью для воспроизведения
) {
    companion object {
        fun fromDeezer(d: DeezerTrack) = Track(
            id = d.id.toString(),
            title = d.title,
            artist = d.artist.name,
            album = d.album.title,
            durationMs = d.duration * 1000L,
            coverUrl = d.album.coverMedium,
            previewUrl = d.previewUrl
        )
    }
}