package com.example.rmp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val screen: Screen
)

val bottomNavItems = listOf(
    BottomNavItem("Главная", Icons.Default.Home,         Screen.Home),
    BottomNavItem("Поиск",   Icons.Default.Search,       Screen.Search),
    BottomNavItem("Медиа",   Icons.Default.LibraryMusic, Screen.Library),
)