package ca.sfu.cmpt362.group4.streamline.ui.movies

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SearchView
import android.widget.Spinner
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentMoviesBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MoviesFragment : Fragment() {
    private lateinit var binding: FragmentMoviesBinding
    private lateinit var moviesAdapter: MoviesAdapter
    private val searchMoviesAdapter =  SearchMovieAdapter()
    private lateinit var sortingSpinner: Spinner

    private lateinit var searchView: SearchView
    private lateinit var searchResultsRecyclerView: RecyclerView

    private lateinit var callback: OnBackPressedCallback

    private val moviesViewModel: MoviesViewModel by viewModels {
        MoviesViewModelFactory(requireContext())
    }

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentMoviesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        searchView = binding.searchView
        searchResultsRecyclerView = binding.searchResultsRecyclerView

        //on movie click
        moviesAdapter = MoviesAdapter(emptyList(), { movie ->
            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie", movie)
                putExtra("previousPageTitle", "Movies")
            }
            startActivity(intent)
        }, R.layout.item_movie)

        binding.recyclerViewMovies.adapter = moviesAdapter
        binding.recyclerViewMovies.layoutManager = GridLayoutManager(context, 2)


        moviesViewModel.movies.observe(viewLifecycleOwner) { movies ->
            if (movies != null) {
                moviesAdapter.updateMovies(movies)
            } else {
                Log.e("MoviesFragment", "Failed to update adapter")
            }
        }


        //handle search
        handleSearch()
        setupSearchResultsRecyclerView()
        handleSpinner()


        return root
    }

    private fun handleSearch(){
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchMovies(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        searchResultsRecyclerView.visibility = View.VISIBLE
                        binding.recyclerViewMovies.visibility = View.GONE
                        searchMovies(it)
                    } else {
                        // Handle when the search query is empty (show all movies, or default list)
                        moviesViewModel.fetchPopularMovies()
                    }
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            // Clear the text in the SearchView
            searchView.setQuery("", false)
            hideKeyboard()
            binding.recyclerViewMovies.visibility = View.VISIBLE
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

    private fun searchMovies(query: String) {
        // Use the ViewModel to perform the search
        lifecycleScope.launch(Dispatchers.IO) {
            Log.d("MoviesFragment", "Searching for movies with query: $query")
            val searchResults = moviesViewModel.searchMoviesByName(query)
            withContext(Dispatchers.Main) {
                Log.d("MoviesFragment", "Search results: $searchResults")
                if (searchResults != null) {
                    searchMoviesAdapter.submitList(searchResults)
                }
            }
        }
    }


    private fun setupSearchResultsRecyclerView() {
        searchResultsRecyclerView.adapter = searchMoviesAdapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(context)

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        dividerItemDecoration.setDrawable(dividerDrawable!!)
        searchResultsRecyclerView.addItemDecoration(dividerItemDecoration)

        // Implement click listener for search results
        searchMoviesAdapter.setOnItemClickListener { movie ->
            // Open HomeMoviesFragment for the selected user
            // Pass the user's UID to HomeMoviesFragment
            searchResultsRecyclerView.visibility = View.GONE
            hideKeyboard()
            searchView.clearFocus()

            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie", movie)
                putExtra("previousPageTitle", "Movies")
            }
            startActivity(intent)
        }


        Log.d("MoviesFragment", "RecyclerView item count: ${searchResultsRecyclerView.adapter?.itemCount}")
    }

    private fun handleSpinner(){
        // Setup sorting Spinner
        val sortingOptions = arrayOf("Popular Movies", "Top Rated Movies", "Now Playing Movies")
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
                    0 -> moviesViewModel.fetchPopularMovies()
                    1 -> moviesViewModel.fetchTopRatedMovies()
                    2 -> moviesViewModel.fetchNowPlayingMovies()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }
    }

}
