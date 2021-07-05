package com.globant.videoplayerproject.api

import com.globant.videoplayerproject.model.StreamUrlVideo
import com.globant.videoplayerproject.utils.URL
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiStreamUrlVideo {
    @GET("tools/streamapi.py")
    fun getVideoUrlAsync(@Query(URL) url: String): Deferred<StreamUrlVideo>
}