package com.example.rmp.ui.navigation

import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Home    : Screen("home")
    object Search  : Screen("search")
    object Library : Screen("library")
    object Profile : Screen("profile")
    object Player  : Screen("player")

    object PlaylistDetail : Screen("playlist/{id}/{name}") {
        fun route(id: Int, name: String) = "playlist/$id/${Uri.encode(name)}"
    }
}