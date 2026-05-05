package com.example.rmp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rmp.data.model.Track
import com.example.rmp.ui.components.TrackRow
import com.example.rmp.ui.player.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    playlistId: Int,
    playlistName: String,
    viewModel: MusicViewModel,
    onTrackClick: (Track) -> Unit,
    onBack: () -> Unit
) {
    val likedIds by viewModel.likedIds.collectAsState()
    val allTracks by viewModel.tracks.collectAsState()
    val trackIds by viewModel.getTrackIdsForPlaylist(playlistId).collectAsState(initial = emptyList())

    val playlistTracks = remember(trackIds, allTracks) {
        allTracks.filter { it.id in trackIds }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlistName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        if (playlistTracks.isEmpty()) {
            Box(Modifier.padding(padding).fillMaxSize()) {
                Text(
                    "Плейлист пуст. Добавьте треки через поиск.",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(playlistTracks, key = { it.id }) { track ->
                    TrackRow(
                        track = track,
                        isLiked = track.id in likedIds,
                        onClick = { onTrackClick(track) },
                        onLike = { viewModel.toggleLike(track.id) },
                        onLongPress = { viewModel.removeTrackFromPlaylist(playlistId, track.id) }
                    )
                }
            }
        }
    }
}