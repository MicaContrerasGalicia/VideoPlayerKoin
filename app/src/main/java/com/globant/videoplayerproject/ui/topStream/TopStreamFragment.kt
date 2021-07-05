package com.globant.videoplayerproject.ui.topStream

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.globant.videoplayerproject.MainActivity
import com.globant.videoplayerproject.R
import com.globant.videoplayerproject.api.SessionManager
import com.globant.videoplayerproject.model.DataStream
import com.globant.videoplayerproject.utils.Utils
import kotlinx.android.synthetic.main.fragment_list_videos.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopStreamFragment : Fragment() {
    private lateinit var adapter: TopStreamAdapter
    private var recyclerView: RecyclerView? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var actionBarCustom: ActionBar

    private var listStreams: MutableList<DataStream> = ArrayList()

    private val topStreamViewModel by viewModel<TopStreamViewModel>()
    private val args: TopStreamFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_top_stream, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        initializeRecyclerView()
        registerListeners()
        registerObservers()

        topStreamViewModel.getListTopStream(
            SessionManager(requireContext()).fetchAuthToken().toString(), args.gameId
        )
    }

    override fun onResume() {
        super.onResume()
        setupToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.options_menu, menu)
        val searchItem = menu.findItem(R.id.search)

        searchItem.setShowAsAction(
            MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItem.SHOW_AS_ACTION_IF_ROOM
        )
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?):Boolean{
                adapter.addStreams(null)
                val newList: MutableList<DataStream> = ArrayList()
                if (newText != null) {
                    for (i in listStreams.indices) {
                        if (listStreams[i].title.toLowerCase().contains(newText)) {
                            newList.add(listStreams[i])
                        }
                    }
                    adapter.addStreams(newList)
                }
                return true
            }
        })
    }

    private fun setupToolbar() {
        with(activity) {
            if (this is MainActivity) actionBarCustom = supportActionBar as ActionBar
            actionBarCustom.setDisplayHomeAsUpEnabled(true)
            activity?.title = args.gameName
        }
    }

    private fun registerListeners() {
        val listResolutions: MutableList<String> = ArrayList()
        listResolutions.addAll(resources.getStringArray(R.array.resolutions))
        var optionSelected: String = ""
        adapter.onClick = {dataStream ->
            Utils().selectOption(
                requireContext(),
                listResolutions,
                object : Utils.OptionCallback<Any> {
                    override fun onOption(option: Any) {
                        this.onOption(option as String)
                    }
                    fun onOption(option: String) {
                        optionSelected = option
                        findNavController().navigate(TopStreamFragmentDirections
                            .navigateToListVideosFragment(listStreams.toTypedArray(), optionSelected, dataStream)
                        )
                    }
                })
        }
    }

    private fun registerObservers() {
        topStreamViewModel.listTopStream.observe(viewLifecycleOwner, Observer {
            adapter.addStreams(null)
            listStreams.removeAll(it)
            listStreams.addAll(it)
            adapter.addStreams(it)
            loading.visibility = View.GONE
        })

        topStreamViewModel.onError.observe(viewLifecycleOwner, Observer {
            Utils().showToast(requireContext(), getString(R.string.error))
            recyclerView?.visibility = View.GONE
        })
    }

    private fun initializeRecyclerView() {
        adapter = TopStreamAdapter()
        recyclerView = view?.findViewById(R.id.stream_recyclerView)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView?.adapter = adapter
    }
}