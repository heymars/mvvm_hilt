package com.s.mvvmdemoapp.ui

import android.app.SearchManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.s.mvvmdemoapp.R
import com.s.mvvmdemoapp.model.Content
import com.s.mvvmdemoapp.model.MoviesResponse
import com.s.mvvmdemoapp.utils.EndlessRecyclerViewScrollListener
import com.s.mvvmdemoapp.utils.ResourceState
import com.s.mvvmdemoapp.utils.SpacesItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_movie_listing.*
import timber.log.Timber


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class MovieListingFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private var gridLayoutManger: GridLayoutManager? = null
    private val viewModel: MovieListingViewModel by viewModels()
    private var scrollListener: EndlessRecyclerViewScrollListener? = null
    var contentList = mutableListOf<Content>()
    private var movieGridAdapter : MovieGridAdapter? = MovieGridAdapter(contentList)
    var isLastPage: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_movie_listing, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        loadInitialData()
        scrollListener?.let {
            recyclerView.addOnScrollListener(it)
        }
        setHasOptionsMenu(true)
        setupObservers()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchManager =
            requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = searchItem.actionView as androidx.appcompat.widget.SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))
        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener,
                androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        if (it.length > 3){
                            Handler().postDelayed({
                                filter(it)
                            }, 200)
                        }
                    }
                    return false
                }
            }
        )
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }
            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                movieGridAdapter?.updateList(contentList)
               return true
            }
        })

    }


    fun filter(text: String) {
        val temp = mutableListOf<Content>()
        for (d in contentList) {
            if (d.name.contains(text, true)) {
                temp.add(d)
            }
        }
        //update recyclerview
        movieGridAdapter?.updateList(temp)
    }

    private fun loadInitialData() {
        contentList.clear()
        movieGridAdapter?.notifyDataSetChanged()
        resetState()
        viewModel.getMovies(0)
        val orientation = resources.configuration.orientation
        val (span, gridLayoutManger) = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Pair(7, GridLayoutManager(requireContext(), 7))
        } else {
            Pair(3, GridLayoutManager(requireContext(), 3))
        }
        recyclerView.apply {
            layoutManager = gridLayoutManger
            addItemDecoration(SpacesItemDecoration( span,32, true))
            adapter = movieGridAdapter
        }
        gridLayoutManger.let {
            scrollListener = object : EndlessRecyclerViewScrollListener(it) {
                override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView) {
                    if (!isLastPage){
                        loadNextData(page)
                    }
                }
            }
        }
    }

    private fun resetState() {
        scrollListener?.resetState()
    }

    fun loadNextData(page:Int){
        viewModel.getMovies(page)
    }

    private fun setupObservers() {
        viewModel.movieListLiveData.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                ResourceState.SUCCESS -> {
                    it.data?.let {
                        bindData(it)
                    }
                }
                ResourceState.ERROR ->
                    Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
                ResourceState.LOADING -> {
                }
            }
        })
    }

    private fun bindData(moviesResponse: MoviesResponse) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = moviesResponse.page.title
        moviesResponse.page.contentItems.content.let {
                if (it.size < 20){
                    isLastPage = true
                }
                this.contentList.addAll(it)
                Timber.d("----------contentList size second---------------------${contentList.size}----------------------------")
                Timber.d("----------Data list size second------------------------${it.size}----------------------------------------")
                recyclerView.post(Runnable {
                    // Notify adapter with appropriate notify methods
                    movieGridAdapter?.notifyItemRangeInserted(
                        movieGridAdapter?.itemCount!!,
                        this.contentList.size - 1
                    )
                })
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MovieListingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}