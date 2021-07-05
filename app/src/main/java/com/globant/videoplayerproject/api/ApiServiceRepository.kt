package com.globant.videoplayerproject.api

import com.globant.videoplayerproject.utils.MAX_VALUE_GAMES

class ApiServiceRepository(private val apiService: ApiService) {
    fun getTopGames(accessToken: String) = apiService.getTopGamesAsync(accessToken, MAX_VALUE_GAMES)
    fun getStreams(accessToken: String, gameId: String) =
        apiService.getTopStreamsAsync(accessToken, gameId)
}