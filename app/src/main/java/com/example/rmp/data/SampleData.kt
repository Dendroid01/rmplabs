package com.example.rmp.data

import com.example.rmp.data.model.Playlist
import com.example.rmp.data.model.Track

object SampleData {

    val tracks = listOf(
        Track(1,  "Blinding Lights",     "The Weeknd",     "After Hours",      200_000L, 0xFF1DB954),
        Track(2,  "Levitating",          "Dua Lipa",       "Future Nostalgia", 203_000L, 0xFFE91E8C),
        Track(3,  "Stay",                "Kid LAROI",       "F*CK LOVE 3",      141_000L, 0xFF2196F3),
        Track(4,  "Peaches",             "Justin Bieber",  "Justice",          198_000L, 0xFFFF9800),
        Track(5,  "Good 4 U",            "Olivia Rodrigo", "SOUR",             178_000L, 0xFF9C27B0),
        Track(6,  "Montero",             "Lil Nas X",      "Montero",          137_000L, 0xFF00BCD4),
        Track(7,  "drivers license",     "Olivia Rodrigo", "SOUR",             242_000L, 0xFF3F51B5),
        Track(8,  "Kiss Me More",        "Doja Cat",       "Planet Her",       208_000L, 0xFFE91E63),
        Track(9,  "Butter",              "BTS",            "Butter",           164_000L, 0xFFFFEB3B),
        Track(10, "Bad Habits",          "Ed Sheeran",     "Bad Habits",       231_000L, 0xFFF44336),
    )

    val playlists = listOf(
        Playlist(1, "Мой микс",          12, 0xFF6200EA),
        Playlist(2, "Вечерний кайф",     8,  0xFF00897B),
        Playlist(3, "Для работы",        15, 0xFF1565C0),
        Playlist(4, "After Hours",       11, 0xFF1DB954, isAlbum = true,  artist = "The Weeknd"),
        Playlist(5, "Future Nostalgia",  11, 0xFFE91E8C, isAlbum = true,  artist = "Dua Lipa"),
        Playlist(6, "SOUR",              11, 0xFF9C27B0, isAlbum = true,  artist = "Olivia Rodrigo"),
    )

    val recentlyPlayed = listOf(tracks[0], tracks[2], tracks[4], tracks[6], tracks[1])
    val liked          = listOf(tracks[0], tracks[3], tracks[8])
}