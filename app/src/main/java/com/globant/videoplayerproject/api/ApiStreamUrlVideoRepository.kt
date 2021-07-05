package com.globant.videoplayerproject.api

class ApiStreamUrlVideoRepository(private val apiStreamUrlVideo: ApiStreamUrlVideo) {
    fun getVideoUrl(url: String) = apiStreamUrlVideo.getVideoUrlAsync(url)
}