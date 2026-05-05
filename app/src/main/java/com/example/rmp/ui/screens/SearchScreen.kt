package com.example.rmp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rmp.data.SampleData
import com.example.rmp.data.model.Track
import com.example.rmp.ui.components.TrackRow
import com.example.rmp.ui.player.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: MusicViewModel,
    onTrackClick: (Track) -> Unit,
    onProfileClick: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val likedIds by viewModel.likedIds.collectAsState()

    val results = remember(query) {
        if (query.isBlank()) SampleData.tracks
        else SampleData.tracks.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.album.contains(query, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Поиск") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Трек, исполнитель или альбом") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            LazyColumn {
                items(results, key = { it.id }) { track ->
                    TrackRow(
                        track = track,
                        isLiked = track.id in likedIds,
                        onClick = { onTrackClick(track) },
                        onLike = { viewModel.toggleLike(track.id) }
                    )
                }
            }
        }
    }
}