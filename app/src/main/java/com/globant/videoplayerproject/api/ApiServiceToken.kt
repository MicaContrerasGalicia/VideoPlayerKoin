package com.globant.videoplayerproject.api

import com.globant.videoplayerproject.model.AccessToken
import com.globant.videoplayerproject.utils.QUERY_CLIENT_ID
import com.globant.videoplayerproject.utils.QUERY_CLIENT_SECRET
import com.globant.videoplayerproject.utils.QUERY_GRANT_TYPE
import kotlinx.coroutines.Deferred
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiServiceToken {
    @POST("token")
    fun getAccessTokenAsync(
        @Query(QUERY_CLIENT_ID) clientId: String,
        @Query(QUERY_CLIENT_SECRET) clientSecret: String,
        @Query(QUERY_GRANT_TYPE) type: String
    ):
            Deferred<AccessToken>
}