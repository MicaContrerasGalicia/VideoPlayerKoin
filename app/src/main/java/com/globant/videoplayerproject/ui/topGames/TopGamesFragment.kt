package com.globant.videoplayerproject.ui.topGames

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.globant.videoplayerproject.MainActivity
import com.globant.videoplayerproject.R
import com.globant.videoplayerproject.api.SessionManager
import com.globant.videoplayerproject.di.viewModel
import com.globant.videoplayerproject.model.Data
import com.globant.videoplayerproject.utils.Utils
import kotlinx.android.synthetic.main.fragment_top_games.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class TopGamesFragment : Fragment() {
    private lateinit var linearLayoutManager: GridLayoutManager
    private lateinit var adapter: TopGamesAdapter
    private var recyclerView: RecyclerView? = null

    private val topGamesViewModel by viewModel<TopGamesViewModel>()

    private var listTopGames: MutableList<Data> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.title = resources.getString(R.string.app_name)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.fragment_top_games, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        initializeRecyclerView()
        registerObservers()
        registerListeners()
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

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.addGames(null)

                val newList: MutableList<Data> = ArrayList()
                if (newText != null) {
                    for (i in listTopGames.indices) {
                        if (listTopGames[i].name.toLowerCase().contains(newText)) {
                            newList.add(listTopGames[i])
                        }
                    }
                    adapter.addGames(newList)
                }
                return true
            }
        })
    }

    private fun initializeRecyclerView() {
        adapter = TopGamesAdapter()
        recyclerView = view?.findViewById(R.id.recyclerView)
        linearLayoutManager = GridLayoutManager(activity, 2)
        recyclerView?.layoutManager = linearLayoutManager
        recyclerView?.adapter = adapter
    }

    private fun setupToolbar() {
        (activity as MainActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        activity?.title = getString(R.string.top_games_title)
    }

    private fun registerListeners() {
        adapter.onClick = {
            findNavController().navigate(
                TopGamesFragmentDirections.navigateToListStreamFragment(
                    it.id,
                    it.name
                )
            )
        }
    }

    private fun registerObservers() {
        topGamesViewModel.accessToken.observe(viewLifecycleOwner, Observer {
            SessionManager(requireContext())
                .saveAuthToken("${Utils().adaptTypeToken(it.token_type)} ${it.access_token}")
            topGamesViewModel.getListGames(
                SessionManager(requireContext()).fetchAuthToken().toString()
            )
        })

        topGamesViewModel.onErrorAccessToken.observe(viewLifecycleOwner, Observer {
            Utils().showDialog(requireContext(),
                getString(R.string.error),
                null,
                getString(R.string.retry),
                { topGamesViewModel.getAccessToken() },
                getString(R.string.cancel),
                { activity?.finish() })
        })

        topGamesViewModel.listGames.observe(viewLifecycleOwner, Observer {
            adapter.addGames(null)
            listTopGames.removeAll(it)
            listTopGames.addAll(it)
            adapter.addGames(it)
            loading.visibility = View.GONE
        })

        topGamesViewModel.onError.observe(viewLifecycleOwner, Observer {
            Utils().showToast(requireContext(), getString(R.string.error))
            message_empty_list_layout.visibility = View.VISIBLE
            recyclerView?.visibility = View.GONE
        })
    }
}