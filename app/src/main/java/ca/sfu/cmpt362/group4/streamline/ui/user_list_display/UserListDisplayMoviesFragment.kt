package ca.sfu.cmpt362.group4.streamline.ui.user_list_display

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.data_models.Movie
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentHomeMoviesBinding
import ca.sfu.cmpt362.group4.streamline.ui.movies.MovieDetailActivity
import ca.sfu.cmpt362.group4.streamline.ui.movies.MoviesAdapter
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserListDisplayMoviesFragment: Fragment() {

    companion object {
        fun newInstance(userId: String): UserListDisplayMoviesFragment {
            val fragment = UserListDisplayMoviesFragment()
            val bundle = Bundle()
            bundle.putString("userId", userId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentHomeMoviesBinding
    private lateinit var moviesAdapter: MoviesAdapter

    private var uid: String? = null

    private val moviesViewModel: MoviesViewModel by viewModels {
        MoviesViewModelFactory(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeMoviesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val arguments = arguments
        uid = arguments?.getString("userId")

        //handle on item click
        moviesAdapter = MoviesAdapter(emptyList(), { movie ->
            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie", movie)
                putExtra("previousPageTitle", "UserListDisplayMovies")
            }
            startActivity(intent)
        }, R.layout.item_movie_home)

        binding.recyclerViewSavedMovies.adapter = moviesAdapter
        binding.recyclerViewSavedMovies.layoutManager = LinearLayoutManager(context)

        //update movie list when data changed
        /* moviesViewModel.savedMovies.observe(viewLifecycleOwner) { savedMovies ->
            if (savedMovies != null) {
                moviesAdapter.updateMovies(savedMovies)
                savedMovies.forEach { movie ->
                    Log.d("HomeMoviesFragment", "Saved Movie: ${movie.roomId}, ${movie.id}, ${movie.title}, Release Date: ${movie.release_date}")

                }
            } else {
                Log.e("HomeMoviesFragment", "Failed to update adapter")
            }
        }*/

        // Retrieve movie data from Firebase and update the adapter
        retrieveMoviesFromFirebase()

        return root
    }

    private fun retrieveMoviesFromFirebase() {
        // Assuming the 'movies' node is under the user's UID in the database

        // Retrieve data from Firebase
        uid?.let {
            FirebaseDatabase.getInstance().reference
                .child("users")
                .child(it)
                .child("movies")
        }?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val moviesList = mutableListOf<Movie>()

                for (movieSnapshot in dataSnapshot.children) {
                    // Parse movie data and add to the list
                    val movie = movieSnapshot.getValue(Movie::class.java)

                    if (movie != null) {
                        moviesList.add(movie)
                    }
                }

                // Update the adapter with the retrieved movies
                moviesAdapter.updateMovies(moviesList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors, if any
                Log.e("Firebase", "Error retrieving movies from Firebase: $databaseError")
                Toast.makeText(requireContext(), "Error retrieving movies", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}