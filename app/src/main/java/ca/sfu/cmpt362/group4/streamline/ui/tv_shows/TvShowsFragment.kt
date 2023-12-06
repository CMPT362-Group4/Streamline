package ca.sfu.cmpt362.group4.streamline.ui.tv_shows

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentTvShowsBinding
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TvShowsFragment : Fragment() {
    private lateinit var binding: FragmentTvShowsBinding
    private lateinit var tvShowsAdapter: TvShowsAdapter
    private val searchTvShowsAdapter = SearchTvShowAdapter()
    private lateinit var sortingSpinner: Spinner

    private lateinit var searchView: SearchView
    private lateinit var searchResultsRecyclerView: RecyclerView

    private lateinit var callback: OnBackPressedCallback

    private val tvShowsViewModel: TvShowsViewModel by viewModels {
        TvShowsViewModelFactory(requireContext())
    }

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentTvShowsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        searchView = binding.searchView
        searchResultsRecyclerView = binding.searchResultsRecyclerView

        //on TV show click
        tvShowsAdapter = TvShowsAdapter(emptyList(), { tvShow ->
            val intent = Intent(context, TvShowDetailActivity::class.java).apply {
                putExtra("tvShows", tvShow)
                putExtra("previousPageTitle", "TvShows")
            }
            startActivity(intent)
        }, R.layout.item_tv_show)

        binding.recyclerViewTvShows.adapter = tvShowsAdapter
        binding.recyclerViewTvShows.layoutManager = GridLayoutManager(context, 2)

        tvShowsViewModel.tvShows.observe(viewLifecycleOwner) { tvShows ->
            if (tvShows != null) {
                tvShowsAdapter.updateTvShows(tvShows)
            } else {
                Log.e("TvShowsFragment", "Failed to update adapter")
            }
        }

        //handle search
        handleSearch()
        setupSearchResultsRecyclerView()
        handleSpinner()

        return root
    }

    private fun handleSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchTvShows(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        searchResultsRecyclerView.visibility = View.VISIBLE
                        binding.recyclerViewTvShows.visibility = View.GONE
                        searchTvShows(it)
                    } else {
                        // Handle when the search query is empty (show all TV shows, or default list)
                        tvShowsViewModel.fetchPopularTvShows()
                    }
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            // Clear the text in the SearchView
            searchView.setQuery("", false)
            hideKeyboard()
            binding.recyclerViewTvShows.visibility = View.VISIBLE
            searchResultsRecyclerView.visibility = View.GONE
            true
        }

        callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Check if the keyboard is currently visible
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isAcceptingText) {
                    // If the keyboard is visible, hide the RecyclerView
                    searchResultsRecyclerView.visibility = View.GONE
                } else {
                    // If the keyboard is not visible, allow the default back button behavior
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }

    private fun searchTvShows(query: String) {
        // Use the ViewModel to perform the search
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("TvShowsFragment", "Searching for TV shows with query: $query")
            val searchResults = tvShowsViewModel.searchTvShowsByName(query)
            withContext(Dispatchers.Main) {
                Log.d("TvShowsFragment", "Search results: $searchResults")
                if (searchResults != null) {
                    searchTvShowsAdapter.submitList(searchResults)
                }
            }
        }
    }

    private fun setupSearchResultsRecyclerView() {
        searchResultsRecyclerView.adapter = searchTvShowsAdapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(context)

        val dividerItemDecoration =
            DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        dividerItemDecoration.setDrawable(dividerDrawable!!)
        searchResultsRecyclerView.addItemDecoration(dividerItemDecoration)

        // Implement click listener for search results
        searchTvShowsAdapter.setOnItemClickListener { tvShow ->
            // Open HomeTvShowsFragment for the selected TV show
            // Pass the user's UID to HomeTvShowsFragment
            searchResultsRecyclerView.visibility = View.GONE
            hideKeyboard()
            searchView.clearFocus()

            val intent = Intent(context, TvShowDetailActivity::class.java).apply {
                putExtra("tvShows", tvShow)
                putExtra("previousPageTitle", "TvShows")
            }
            startActivity(intent)
        }

        Log.d(
            "TvShowsFragment",
            "RecyclerView item count: ${searchResultsRecyclerView.adapter?.itemCount}"
        )
    }

    private fun handleSpinner() {
        // Setup sorting Spinner
        val sortingOptions = arrayOf("Popular TV Shows", "Top Rated TV Shows", "Now Playing TV Shows")
        sortingSpinner = binding.root.findViewById<Spinner>(R.id.sortingSpinner)
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.item_sort_spinner, // Use your custom layout
            sortingOptions
        )
        sortingSpinner.adapter = adapter

        sortingSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Handle the selected sorting option
                when (position) {
                    0 -> tvShowsViewModel.fetchPopularTvShows()
                    1 -> tvShowsViewModel.fetchTopRatedTvShows()
                    2 -> tvShowsViewModel.fetchOnTheAirTvShows()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }
}
