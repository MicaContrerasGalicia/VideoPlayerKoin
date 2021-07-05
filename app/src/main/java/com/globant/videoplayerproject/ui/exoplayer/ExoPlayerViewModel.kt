package com.globant.videoplayerproject.ui.exoplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.globant.videoplayerproject.api.ApiStreamUrlVideoRepository
import com.globant.videoplayerproject.model.Url
import com.globant.videoplayerproject.utils.TWITCH_BASE_URL
import kotlinx.coroutines.launch

class ExoPlayerViewModel(private val apiServiceVideoRepository: ApiStreamUrlVideoRepository) :
    ViewModel() {

    private val _listURL = MutableLiveData<Url>()
    private val _onError = MutableLiveData<Boolean>()

    val listURL: LiveData<Url>
        get() = _listURL

    val onError: LiveData<Boolean>
        get() = _onError

    fun getUrls(user_name: String) {
        viewModelScope.launch {
            val getPropertiesDeferred = apiServiceVideoRepository.getVideoUrl(TWITCH_BASE_URL + user_name)
            try {
                val urls = getPropertiesDeferred.await()
                _listURL.value = urls.urls
            } catch (e: Exception) {
                _onError.value = true
            }
        }
    }
}