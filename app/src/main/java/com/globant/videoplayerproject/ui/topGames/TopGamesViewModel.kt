package com.globant.videoplayerproject.ui.topGames

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globant.videoplayerproject.api.ApiServiceRepository
import com.globant.videoplayerproject.api.ApiServiceTokenRepository
import com.globant.videoplayerproject.model.AccessToken
import com.globant.videoplayerproject.model.Data
import kotlinx.coroutines.launch

class TopGamesViewModel(
    private val apiServiceRepository: ApiServiceRepository,
    private val apiServiceTokenRepository: ApiServiceTokenRepository
) : ViewModel() {

    private val _accessToken = MutableLiveData<AccessToken>()
    private val _onErrorAccessToken = MutableLiveData<Boolean>()
    private val _listGames = MutableLiveData<List<Data>>()
    private val _onError = MutableLiveData<Boolean>()

    val accessToken: LiveData<AccessToken>
        get() = _accessToken

    val onErrorAccessToken: LiveData<Boolean>
        get() = _onErrorAccessToken

    val listGames: LiveData<List<Data>>
        get() = _listGames

    val onError: LiveData<Boolean>
        get() = _onError

    init {
        getAccessToken()
    }

    fun getAccessToken() {
        viewModelScope.launch {
            val getPropertiesDeferred = apiServiceTokenRepository.getTokens()
            try {
                val accessToken = getPropertiesDeferred.await()
                _accessToken.value = accessToken
            } catch (e: Exception) {
                _onErrorAccessToken.value = true
            }
        }
    }

    fun getListGames(accessToken: String) {
        viewModelScope.launch {
            val getPropertiesDeferred = apiServiceRepository.getTopGames(accessToken)
            try {
                val topGame = getPropertiesDeferred.await()
                _listGames.value = topGame.data
            } catch (e: Exception) {
                _onError.value = true
            }
        }
    }
}