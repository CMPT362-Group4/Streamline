package ca.sfu.cmpt362.group4.streamline.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentHomeBinding
import ca.sfu.cmpt362.group4.streamline.repositories.MoviesRepository
import ca.sfu.cmpt362.group4.streamline.room.DAOs.MovieDao
import ca.sfu.cmpt362.group4.streamline.room.databases.MovieDatabase
import ca.sfu.cmpt362.group4.streamline.ui.movies.MovieDetailActivity
import ca.sfu.cmpt362.group4.streamline.ui.movies.MoviesAdapter
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var addButton: FloatingActionButton

    private lateinit var moviesViewModel: MoviesViewModel
    private lateinit var movieDao: MovieDao
    private lateinit var moviesRepository: MoviesRepository

    private lateinit var moviesAdapter: MoviesAdapter
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)

        //add logo
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(true)

        addButton = binding.addButton
        handleAddButton()

        //initialize database
        movieDao = MovieDatabase.getInstance(requireContext()).movieDao
        moviesRepository = MoviesRepository(movieDao)
        moviesViewModel = ViewModelProvider(this, MoviesViewModelFactory(moviesRepository))[MoviesViewModel::class.java]

        //handle on item click
        moviesAdapter = MoviesAdapter(emptyList(), { movie ->
            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie", movie)
                putExtra("previousPageTitle", "Home")
            }
            startActivity(intent)
        }, R.layout.item_movie_home)

        binding.recyclerViewSavedMovies.adapter = moviesAdapter
        binding.recyclerViewSavedMovies.layoutManager = LinearLayoutManager(context)

        //update movie list when data changed
        moviesViewModel.savedMovies.observe(viewLifecycleOwner) { savedMovies ->
            if (savedMovies != null) {
                moviesAdapter.updateMovies(savedMovies)
                savedMovies?.forEach { movie ->
                    Log.d("HomeFragment", "Saved Movie: ${movie.databaseId}, ${movie.id}, ${movie.title}, Release Date: ${movie.release_date}")

                }
            } else {
                Log.e("HomeFragment", "Failed to update adapter")
            }
        }

        return root
    }

    private fun handleAddButton(){
        addButton.setOnClickListener(){
            Log.d("Home", "button clicked")
            ChooseMediaTypeDialogFragment().show(parentFragmentManager, "chooseMediaType")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all -> {
                // Handle delete all button click
                showConfirmationDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to delete all entries?")
            .setPositiveButton("OK") { _, _ ->
                moviesViewModel.deleteAllMovies()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}