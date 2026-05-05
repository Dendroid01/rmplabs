package com.example.rmp.data.network

import com.example.rmp.data.model.DeezerSearchResponse
import com.example.rmp.data.model.DeezerTrack
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("limit") limit: Int = 25
    ): DeezerSearchResponse

    @GET("chart/0/tracks")
    suspend fun getChartTracks(
        @Query("limit") limit: Int = 25
    ): DeezerSearchResponse

    @GET("track/{id}")
    suspend fun getTrack(@Path("id") id: Long): DeezerTrack
}