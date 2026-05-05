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
    val likedIds   by viewModel.likedIds.collectAsState()
    val tracks     by viewModel.tracks.collectAsState()
    val isLoading  by viewModel.isLoading.collectAsState()
    var trackForPlaylist by remember { mutableStateOf<Track?>(null) }
    val playlists  by viewModel.playlists.collectAsState()

    // Запускаем поиск через Deezer API с debounce,
    // при пустом запросе — возвращаем чарты
    LaunchedEffect(query) {
        if (query.isBlank()) {
            viewModel.loadCharts()
        } else {
            kotlinx.coroutines.delay(400)   // debounce: не шлём запрос на каждый символ
            if (query.isNotBlank()) viewModel.search(query)
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
                trailingIcon = {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (query.isBlank()) {
                Text(
                    text = "Чарты",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            LazyColumn {
                items(tracks, key = { it.id }) { track ->
                    TrackRow(
                        track = track,
                        isLiked = track.id in likedIds,
                        onClick = { onTrackClick(track) },
                        onLike = { viewModel.toggleLike(track.id) },
                        onAddToPlaylist = { trackForPlaylist = track }
                    )
                }
            }
        }
    }

    trackForPlaylist?.let { track ->
        AlertDialog(
            onDismissRequest = { trackForPlaylist = null },
            title = { Text("Добавить в плейлист") },
            text = {
                if (playlists.isEmpty()) {
                    Text("Сначала создайте плейлист в Медиатеке")
                } else {
                    LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                        items(playlists) { playlist ->
                            TextButton(
                                onClick = {
                                    viewModel.addTrackToPlaylist(playlist.id, track.id)
                                    trackForPlaylist = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(playlist.name)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { trackForPlaylist = null }) { Text("Закрыть") }
            }
        )
    }
}