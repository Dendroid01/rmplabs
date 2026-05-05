package com.example.rmp.repository

import com.example.rmp.data.model.Track
import com.example.rmp.data.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TrackRepository {
    private val api = RetrofitClient.deezerApi

    suspend fun search(query: String): Result<List<Track>> = withContext(Dispatchers.IO) {
        runCatching {
            api.search(query).data.map { Track.fromDeezer(it) }
        }
    }

    suspend fun getCharts(): Result<List<Track>> = withContext(Dispatchers.IO) {
        runCatching {
            api.getChartTracks().data.map { Track.fromDeezer(it) }
        }
    }
}