package com.globant.videoplayerproject.model

data class AccessToken(
    val access_token: String,
    val expires_in: String,
    val token_type: String
)