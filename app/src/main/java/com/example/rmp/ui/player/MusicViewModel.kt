package com.example.rmp.ui.player

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.rmp.data.model.Track
import com.example.rmp.data.model.UserPlaylist
import com.example.rmp.data.model.UserTrackLike
import com.example.rmp.data.model.PlaylistTrack
import com.example.rmp.data.storage.AppDatabase
import com.example.rmp.player.PlaybackService
import com.example.rmp.repository.TrackRepository
import com.example.rmp.session.SessionManager
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.rmp.repository.UserRepository

data class PlayerState(
    val currentTrack: Track? = null,
    val isPlaying: Boolean = false,
    val progressMs: Long = 0L,
    val durationMs: Long = 0L,
    val queue: List<Track> = emptyList()
)

class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val likeDao = db.userTrackLikeDao()
    private val playlistDao = db.playlistDao()
    private val session = SessionManager(application)
    private val trackRepo = TrackRepository()

    val userId: Int get() = session.getCurrentUserId()

    // --- Player state ---
    private val _player = MutableStateFlow(PlayerState())
    val player: StateFlow<PlayerState> = _player.asStateFlow()

    // --- Liked IDs (персональные для юзера) ---
    val likedIds: StateFlow<Set<String>> = likeDao
        .getLikedTrackIds(userId)
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    // --- Playlists ---
    val playlists: StateFlow<List<UserPlaylist>> = playlistDao
        .getPlaylistsForUser(userId)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    // --- Tracks (charts + search) ---
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _history = MutableStateFlow<List<Track>>(emptyList())
    val history: StateFlow<List<Track>> = _history.asStateFlow()

    // --- ExoPlayer MediaController ---
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    init {
        loadCharts()
        connectPlayer(application)
        startProgressTicker()
    }

    private fun connectPlayer(application: Application) {
        val token = SessionToken(
            application,
            ComponentName(application, PlaybackService::class.java)
        )
        controllerFuture = MediaController.Builder(application, token).buildAsync()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _player.update { it.copy(isPlaying = isPlaying) }
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    _player.update { it.copy(progressMs = newPosition.positionMs) }
                }
            })
        }, { it.run() })
    }

    // --- Charts ---
    fun loadCharts() {
        viewModelScope.launch {
            _isLoading.value = true
            trackRepo.getCharts().onSuccess { _tracks.value = it }
            _isLoading.value = false
        }
    }

    // --- Search ---
    fun search(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            trackRepo.search(query).onSuccess { _tracks.value = it }
            _isLoading.value = false
        }
    }

    // --- Playback ---
    fun play(track: Track) {
        _player.update { it.copy(currentTrack = track, isPlaying = true, queue = tracks.value) }
        addToHistory(track)

        if (track.previewUrl.isNotEmpty()) {
            val item = MediaItem.Builder()
                .setMediaId(track.id)
                .setUri(track.previewUrl)
                .build()
            controller?.apply {
                setMediaItem(item)
                prepare()
                play()
            }
        }
    }

    fun togglePlayPause() {
        controller?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
        _player.update { it.copy(isPlaying = !(it.isPlaying)) }
    }

    fun skipNext() {
        val queue = _player.value.queue
        val cur = _player.value.currentTrack ?: return
        val next = queue[(queue.indexOfFirst { it.id == cur.id } + 1) % queue.size]
        play(next)
    }

    fun skipPrev() {
        val queue = _player.value.queue
        val cur = _player.value.currentTrack ?: return
        val idx = queue.indexOfFirst { it.id == cur.id }
        play(queue[(idx - 1 + queue.size) % queue.size])
    }

    fun seekTo(ms: Long) {
        controller?.seekTo(ms)
        _player.update { it.copy(progressMs = ms) }
    }

    // --- Likes ---
    fun toggleLike(trackId: String) {
        viewModelScope.launch {
            val like = UserTrackLike(userId, trackId)
            if (trackId in likedIds.value) likeDao.unlike(like)
            else likeDao.like(like)
        }
    }

    // --- Playlists ---
    fun createPlaylist(name: String) {
        viewModelScope.launch {
            playlistDao.createPlaylist(UserPlaylist(userId = userId, name = name))
        }
    }

    fun deletePlaylist(playlist: UserPlaylist) {
        viewModelScope.launch { playlistDao.deletePlaylist(playlist) }
    }

    fun renamePlaylist(id: Int, newName: String) {
        viewModelScope.launch { playlistDao.renamePlaylist(id, newName) }
    }

    fun addTrackToPlaylist(playlistId: Int, trackId: String) {
        viewModelScope.launch {
            playlistDao.addTrackToPlaylist(PlaylistTrack(playlistId, trackId))
        }
    }

    fun removeTrackFromPlaylist(playlistId: Int, trackId: String) {
        viewModelScope.launch {
            playlistDao.removeTrackFromPlaylist(playlistId, trackId)
        }
    }

    fun getTrackIdsForPlaylist(playlistId: Int): Flow<List<String>> =
        playlistDao.getTrackIdsForPlaylist(playlistId)

    private fun addToHistory(track: Track) {
        _history.update { list ->
            (listOf(track) + list.filter { it.id != track.id }).take(20).toMutableList()
        }
    }

    override fun onCleared() {
        MediaController.releaseFuture(controllerFuture ?: return)
        super.onCleared()
    }

    private fun startProgressTicker() {
        viewModelScope.launch {
            while (true) {
                delay(500)
                controller?.let { c ->
                    if (c.isPlaying) {
                        _player.update {
                            it.copy(
                                progressMs = c.currentPosition,
                                durationMs = c.duration.coerceAtLeast(0)
                            )
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        controller?.stop()
        controller?.clearMediaItems()
        _player.update { PlayerState() }
        val repo = UserRepository(getApplication())
        repo.logout()
    }
}