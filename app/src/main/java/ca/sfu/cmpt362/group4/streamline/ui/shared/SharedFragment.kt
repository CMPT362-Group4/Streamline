package ca.sfu.cmpt362.group4.streamline.ui.shared

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentSharedBinding
import ca.sfu.cmpt362.group4.streamline.ui.movies.MovieDetailActivity
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class SharedFragment : Fragment() {

    private lateinit var binding: FragmentSharedBinding
    private lateinit var sharedAdapter: SharedAdapter

    private val moviesViewModel: MoviesViewModel by viewModels {
        MoviesViewModelFactory(requireContext())
    }

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSharedBinding.inflate(inflater, container, false)
        val root: View = binding.root


        sharedAdapter = SharedAdapter(emptyList(), { movie ->
            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie", movie)
                putExtra("previousPageTitle", "Movies")
            }
            startActivity(intent)
        }, R.layout.item_movie_home)

        binding.recyclerViewShared.adapter = sharedAdapter
        binding.recyclerViewShared.layoutManager = LinearLayoutManager(context)

        moviesViewModel.savedMovies.observe(viewLifecycleOwner) { savedMovies ->
            if (savedMovies != null) {
                sharedAdapter.updateSharedContent(savedMovies)
                savedMovies.forEach { movie ->
                    Log.d("HomeMoviesFragment", "Saved Movie: ${movie.roomId}, ${movie.id}, ${movie.title}, Release Date: ${movie.release_date}")

                }
            } else {
                Log.e("HomeMoviesFragment", "Failed to update adapter")
            }
        }

        return root
    }

}