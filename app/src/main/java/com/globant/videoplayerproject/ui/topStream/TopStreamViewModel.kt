package com.globant.videoplayerproject.ui.topStream

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globant.videoplayerproject.api.ApiServiceRepository
import com.globant.videoplayerproject.model.DataStream
import kotlinx.coroutines.launch

class TopStreamViewModel(private val apiServiceRepository: ApiServiceRepository) :
    ViewModel() {

    private val _listTopStream = MutableLiveData<List<DataStream>>()
    private val _onError = MutableLiveData<Boolean>()

    val listTopStream: LiveData<List<DataStream>>
        get() = _listTopStream

    val onError: LiveData<Boolean>
        get() = _onError

    fun getListTopStream(accessToken: String, gameId: String) {
        viewModelScope.launch {
            val getPropertiesDeferred = apiServiceRepository.getStreams(accessToken, gameId)
            try {
                val topGame = getPropertiesDeferred.await()
                _listTopStream.value = topGame.data
            } catch (e: Exception) {
                _onError.value = true
            }
        }
    }
}