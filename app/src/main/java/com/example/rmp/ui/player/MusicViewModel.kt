package com.example.rmp.ui.player

import androidx.lifecycle.ViewModel
import com.example.rmp.data.SampleData
import com.example.rmp.data.model.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val progressMs: Long = 0L,
    val queue: List<Track> = SampleData.tracks
)

class MusicViewModel : ViewModel() {

    private val _player = MutableStateFlow(PlayerState())
    val player: StateFlow<PlayerState> = _player.asStateFlow()

    private val _likedIds = MutableStateFlow(SampleData.liked.map { it.id }.toMutableSet())
    val likedIds: StateFlow<Set<Int>> = _likedIds.asStateFlow()

    private val _history = MutableStateFlow(SampleData.recentlyPlayed.toMutableList())
    val history: StateFlow<List<Track>> = _history.asStateFlow()

    fun play(track: Track) {
        _player.update { it.copy(currentTrack = track, isPlaying = true, progressMs = 0L) }
        addToHistory(track)
    }

    fun togglePlayPause() {
        _player.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun skipNext() {
        val queue = _player.value.queue
        val cur   = _player.value.currentTrack ?: return
        val next  = queue[(queue.indexOfFirst { it.id == cur.id } + 1) % queue.size]
        play(next)
    }

    fun skipPrev() {
        val queue = _player.value.queue
        val cur   = _player.value.currentTrack ?: return
        val idx   = queue.indexOfFirst { it.id == cur.id }
        val prev  = queue[(idx - 1 + queue.size) % queue.size]
        play(prev)
    }

    fun seekTo(ms: Long) {
        _player.update { it.copy(progressMs = ms) }
    }

    fun toggleLike(trackId: Int) {
        _likedIds.update { set ->
            set.toMutableSet().also {
                if (trackId in it) it.remove(trackId) else it.add(trackId)
            }
        }
    }

    fun isLiked(trackId: Int) = trackId in _likedIds.value

    private fun addToHistory(track: Track) {
        _history.update { list ->
            (listOf(track) + list.filter { it.id != track.id }).take(20).toMutableList()
        }
    }
}