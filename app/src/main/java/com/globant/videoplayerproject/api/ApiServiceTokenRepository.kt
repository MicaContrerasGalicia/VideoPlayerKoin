package com.globant.videoplayerproject.api

import com.globant.videoplayerproject.utils.CLIENT_ID
import com.globant.videoplayerproject.utils.CLIENT_SECRET
import com.globant.videoplayerproject.utils.GRANT_TYPE

class ApiServiceTokenRepository(private val apiServiceToken: ApiServiceToken) {
    fun getTokens() = apiServiceToken.getAccessTokenAsync(CLIENT_ID, CLIENT_SECRET, GRANT_TYPE)
}