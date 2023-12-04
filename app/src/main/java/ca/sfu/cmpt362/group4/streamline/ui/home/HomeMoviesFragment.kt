package ca.sfu.cmpt362.group4.streamline.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentHomeMoviesBinding
import ca.sfu.cmpt362.group4.streamline.repositories.MoviesRepository
import ca.sfu.cmpt362.group4.streamline.room.DAOs.MovieDao
import ca.sfu.cmpt362.group4.streamline.room.databases.MovieDatabase
import ca.sfu.cmpt362.group4.streamline.ui.movies.MovieDetailActivity
import ca.sfu.cmpt362.group4.streamline.ui.movies.MoviesAdapter
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory

class HomeMoviesFragment : Fragment(), DeleteAllHandler {

    private lateinit var binding: FragmentHomeMoviesBinding
    private lateinit var moviesAdapter: MoviesAdapter

    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var movieDao: MovieDao
    private lateinit var moviesRepository: MoviesRepository


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeMoviesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //initialize database
        movieDao = MovieDatabase.getInstance(requireContext()).movieDao
        moviesRepository = MoviesRepository(movieDao)
        moviesViewModel = ViewModelProvider(this, MoviesViewModelFactory(moviesRepository))[MoviesViewModel::class.java]



        //handle on item click
        moviesAdapter = MoviesAdapter(emptyList(), { movie ->
            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie", movie)
                putExtra("previousPageTitle", "HomeMovies")
            }
            startActivity(intent)
        }, R.layout.item_movie_home)

        binding.recyclerViewSavedMovies.adapter = moviesAdapter
        binding.recyclerViewSavedMovies.layoutManager = LinearLayoutManager(context)

        //update movie list when data changed
        moviesViewModel.savedMovies.observe(viewLifecycleOwner) { savedMovies ->
            if (savedMovies != null) {
                moviesAdapter.updateMovies(savedMovies)
                savedMovies.forEach { movie ->
                    Log.d("HomeMoviesFragment", "Saved Movie: ${movie.databaseId}, ${movie.id}, ${movie.title}, Release Date: ${movie.release_date}")

                }
            } else {
                Log.e("HomeMoviesFragment", "Failed to update adapter")
            }
        }

        return root
    }

    override fun handleDeleteAll() {
        // Implement logic to delete all movies using moviesViewModel
        moviesViewModel.deleteAllMovies()
        Toast.makeText(requireContext(), "All movies deleted from your list", Toast.LENGTH_SHORT).show()
    }
}