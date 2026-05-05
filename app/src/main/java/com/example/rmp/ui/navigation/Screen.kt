package com.example.rmp.ui.navigation

sealed class Screen(val route: String) {
    object Home    : Screen("home")
    object Search  : Screen("search")
    object Library : Screen("library")
    object Profile : Screen("profile")
    object Player  : Screen("player")
}