package com.globant.videoplayerproject.api

import com.globant.videoplayerproject.model.Stream
import com.globant.videoplayerproject.model.TopGames
import com.globant.videoplayerproject.utils.*
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {
    @Headers(HEADER_API_KEY)
    @GET("helix/games/top")
    fun getTopGamesAsync(
        @Header(HEADER_AUTHORIZATION) accessToken: String,
        @Query(QUERY_FIRST_RESULTS) gamesToLoad: Int
    ):
            Deferred<TopGames>

    @Headers(HEADER_API_KEY)
    @GET("helix/streams")
    fun getTopStreamsAsync(
        @Header(HEADER_AUTHORIZATION) accessToken: String,
        @Query(QUERY_GAME_ID) gameId: String
    ):
            Deferred<Stream>
}