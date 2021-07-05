package com.globant.videoplayerproject.ui.videos

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.globant.videoplayerproject.MainActivity
import com.globant.videoplayerproject.R
import com.globant.videoplayerproject.model.DataStream
import com.globant.videoplayerproject.model.FullscreenMessage
import com.globant.videoplayerproject.model.NextVideoSelected
import com.globant.videoplayerproject.ui.exoplayer.ExoPlayerFragment
import kotlinx.android.synthetic.main.fragment_list_videos.*
import kotlinx.android.synthetic.main.fragment_top_stream.loading
import kotlinx.android.synthetic.main.fragment_top_stream.stream_recyclerView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.roundToInt

class ListVideosFragment : Fragment() {
    private lateinit var adapter: ListVideoAdapter
    private var recyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var actionBarCustom: ActionBar

    private lateinit var selectedStream: DataStream

    private val args: ListVideosFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_list_videos, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedStream = args.streamSelected

        initializeRecyclerView()
        registerListeners()
        loadStreamsList()
    }

    override fun onResume() {
        super.onResume()
        setupToolbar()

        inflateExoPlayerFragment(null)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    private fun inflateExoPlayerFragment(userName: String?) {
        val ft = childFragmentManager.beginTransaction()
        val exoPlayerFragment = ExoPlayerFragment()
        val arguments = Bundle()
        if (userName == null) {
            arguments.putString(USER_NAME, args.streamSelected.user_name)
        } else {
            arguments.putString(USER_NAME, userName)
        }
        arguments.putString(DEFINITION, args.resolution)
        exoPlayerFragment.arguments = arguments
        ft.add(R.id.video_exo_player, exoPlayerFragment)
        ft.commit()
    }

    private fun setupToolbar(userName: String? = null) {
        with(activity) {
            if (this is MainActivity) actionBarCustom = supportActionBar as ActionBar
            actionBarCustom.setDisplayHomeAsUpEnabled(true)
            val channelName = userName ?: args.streamSelected.user_name
            activity?.title = "$channelName ${getString(R.string.channel)}"
        }
    }

    private fun registerListeners() {
        adapter.onClick = {
            EventBus.getDefault().post(NextVideoSelected(true))
            setupToolbar(it.user_name)
            inflateExoPlayerFragment(it.user_name)
            selectedStream = it
            loadStreamsList()
        }
    }

    private fun loadStreamsList() {
        val newListStreams: MutableList<DataStream> = ArrayList()
        newListStreams.addAll(args.listStreams)
        newListStreams.remove(selectedStream)
        adapter.addStreams(newListStreams)
        loading.visibility = View.GONE
    }

    private fun initializeRecyclerView() {
        adapter = ListVideoAdapter()
        recyclerView = view?.findViewById(R.id.stream_recyclerView)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: FullscreenMessage) {
        if (event.fullscreen) {
            with(activity) {
                if (this is MainActivity) {
                    if (supportActionBar != null) {
                        supportActionBar?.show()
                        setupToolbar()
                    }
                }
            }

            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            val params = video_exo_player.layoutParams as LinearLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = resources.getDimension(R.dimen.wrap_constraint).roundToInt()
            video_exo_player.layoutParams = params
            stream_recyclerView.visibility = View.VISIBLE
        } else {
            with(activity) {
                if (this is MainActivity) {
                    if (supportActionBar != null) {
                        supportActionBar?.hide()
                    }
                }
            }
            activity?.window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_IMMERSIVE
                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            val params = video_exo_player.layoutParams as LinearLayout.LayoutParams
            params.width = ViewGroup.LayoutParams.MATCH_PARENT
            params.height = ViewGroup.LayoutParams.MATCH_PARENT
            video_exo_player.layoutParams = params
            stream_recyclerView.visibility = View.GONE
        }
    }

    companion object {
        const val USER_NAME = "user_name"
        const val DEFINITION = "definition"
    }
}