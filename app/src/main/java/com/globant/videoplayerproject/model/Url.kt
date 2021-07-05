package com.globant.videoplayerproject.model

import com.google.gson.annotations.SerializedName

data class Url(
    val audio_only: String,
    @SerializedName("160p")
    val _160p: String,
    @SerializedName("360p")
    val _360p: String,
    @SerializedName("480p")
    val _480p: String,
    @SerializedName("720p")
    val _720p: String,
    @SerializedName("720p60")
    val _720p60: String,
    @SerializedName("1080p60")
    val _1080p60: String
)
