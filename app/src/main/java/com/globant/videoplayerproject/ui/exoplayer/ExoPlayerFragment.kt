package com.globant.videoplayerproject.ui.exoplayer

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.globant.videoplayerproject.MainActivity
import com.globant.videoplayerproject.R
import com.globant.videoplayerproject.model.FullscreenMessage
import com.globant.videoplayerproject.model.NextVideoSelected
import com.globant.videoplayerproject.model.Url
import com.globant.videoplayerproject.utils.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.fragment_exo_player.*
import kotlinx.android.synthetic.main.simple_custom_controls.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.koin.androidx.viewmodel.ext.android.viewModel

class ExoPlayerFragment : Fragment() {
    private lateinit var url: Url
    private var exoPlayer: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    private val exoPlayerViewModel by viewModel<ExoPlayerViewModel>()
    private var userName: String? = null
    private var definition: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_exo_player, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadData()
        registerObservers()

        if (userName != null) {
            exoPlayerViewModel.getUrls(userName.toString())
        }

        setupFullscreenButton()
        setupSettingsButton()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun setupSettingsButton() {
        exo_settings.setOnClickListener {
            val listResolutions: MutableList<String> = ArrayList()
            listResolutions.addAll(resources.getStringArray(R.array.resolutions))
            Utils().selectOption(
                requireContext(),
                listResolutions,
                object : Utils.OptionCallback<Any> {
                    override fun onOption(option: Any) {
                        this.onOption(option as String)
                    }

                    fun onOption(option: String) {
                        definition = option
                        initializePlayer(checkDefinition())
                    }
                })
        }
    }

    private fun setupFullscreenButton() {
        var fullscreen = false
        exo_fullscreen.setOnClickListener {
            if (fullscreen) {
                exo_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.exo_ic_fullscreen_enter
                    )
                )
                EventBus.getDefault().post(FullscreenMessage(fullscreen))
                fullscreen = false
            } else {
                exo_fullscreen.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.exo_ic_fullscreen_exit
                    )
                )
                EventBus.getDefault().post(FullscreenMessage(fullscreen))
                fullscreen = true
            }
        }
    }

    private fun loadData() {
        val args = arguments
        userName = args?.getString(USER_NAME)
        definition = args?.getString(DEFINITION)
    }

    private fun registerObservers() {
        exoPlayerViewModel.listURL.observe(viewLifecycleOwner, Observer {
            hideSystemUi()
            url = it
            initializePlayer(checkDefinition())
        })
        exoPlayerViewModel.onError.observe(viewLifecycleOwner, Observer {
            Utils().showToast(requireContext(), getString(R.string.error))
        })
    }

    private fun checkDefinition(): String {
        return when (definition) {
            _160P -> this.url._160p
            _360P -> this.url._360p
            _480P -> this.url._480p
            _720P -> this.url._720p
            _720P60 -> this.url._720p60
            _1080P60 -> this.url._1080p60
            else -> this.url.audio_only
        }
    }

    private fun initializePlayer(url: String) {
        val trackSelector = DefaultTrackSelector(requireContext())
        trackSelector.setParameters(
            trackSelector.buildUponParameters().setMaxVideoSizeSd()
        )
        val url = Uri.parse(url)
        val mediaItem = MediaItem.Builder()
            .setUri(url)
            .setMimeType(MimeTypes.APPLICATION_M3U8)
            .build()
        exoPlayer = SimpleExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .setMediaSourceFactory(DefaultMediaSourceFactory(context as Context))
            .build()
        video_view?.player = exoPlayer
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.seekTo(currentWindow, playbackPosition)
        exoPlayer?.playWhenReady = playWhenReady
    }

    private fun releasePlayer() {
        if (exoPlayer != null) {
            playWhenReady = exoPlayer?.playWhenReady ?: false
            playbackPosition = exoPlayer?.currentPosition ?: 0
            currentWindow = exoPlayer?.currentWindowIndex ?: 0
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    private fun hideSystemUi() {
        video_view!!.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: NextVideoSelected) {
        if (event.newVideoSelected) {
            releasePlayer()
        }
    }

    companion object {
        const val USER_NAME = "user_name"
        const val DEFINITION = "definition"
    }
}