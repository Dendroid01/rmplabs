package com.example.rmp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.rmp.ui.components.MiniPlayer
import com.example.rmp.ui.navigation.Screen
import com.example.rmp.ui.navigation.bottomNavItems
import com.example.rmp.ui.player.MusicViewModel
import com.example.rmp.ui.screens.*

@Composable
fun AppNavigation(username: String) {
    val navController = rememberNavController()
    val musicViewModel: MusicViewModel = viewModel()
    val playerState by musicViewModel.player.collectAsState()
    val likedIds    by musicViewModel.likedIds.collectAsState()

    val currentRoute by navController.currentBackStackEntryAsState()
    val showBottomBar = currentRoute?.destination?.route != Screen.Player.route

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                Column {
                    playerState.currentTrack?.let { track ->
                        MiniPlayer(
                            track = track,
                            isPlaying = playerState.isPlaying,
                            isLiked = track.id in likedIds,
                            onTogglePlay = { musicViewModel.togglePlayPause() },
                            onLike = { musicViewModel.toggleLike(track.id) },
                            onClick = { navController.navigate(Screen.Player.route) }
                        )
                    }

                    NavigationBar {
                        val route = currentRoute?.destination?.route
                        bottomNavItems.forEach { item ->
                            NavigationBarItem(
                                selected = route == item.screen.route,
                                onClick = {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(Screen.Home.route) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    viewModel = musicViewModel,
                    onTrackClick = { track ->
                        musicViewModel.play(track)
                        navController.navigate(Screen.Player.route)
                    },
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    viewModel = musicViewModel,
                    onTrackClick = { track ->
                        musicViewModel.play(track)
                        navController.navigate(Screen.Player.route)
                    },
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.Library.route) {
                LibraryScreen(
                    onProfileClick = { navController.navigate(Screen.Profile.route) }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    viewModel = musicViewModel,
                    username = username,
                    onBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Player.route) {
                PlayerScreen(
                    viewModel = musicViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}