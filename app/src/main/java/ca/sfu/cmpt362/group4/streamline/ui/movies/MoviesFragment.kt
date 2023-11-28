package ca.sfu.cmpt362.group4.streamline.ui.movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentMoviesBinding
import ca.sfu.cmpt362.group4.streamline.repositories.MoviesRepository
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory
import android.content.Intent


class MoviesFragment : Fragment() {
    private lateinit var binding: FragmentMoviesBinding
    private lateinit var moviesAdapter: MoviesAdapter
    private lateinit var moviesViewModel: MoviesViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentMoviesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        moviesAdapter = MoviesAdapter(emptyList()) { movie ->
            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("movie", movie)
            }
            startActivity(intent)
        }

        binding.recyclerViewMovies.adapter = moviesAdapter
        binding.recyclerViewMovies.layoutManager = GridLayoutManager(context, 2)

        val moviesRepository = MoviesRepository()
        moviesViewModel = ViewModelProvider(this, MoviesViewModelFactory(moviesRepository)).get(MoviesViewModel::class.java)

        moviesViewModel.movies.observe(viewLifecycleOwner) { movies ->
            if (movies != null) {
                moviesAdapter.updateMovies(movies)
            } else {
                Log.e("MoviesFragment", "Failed to update adapter")
            }
        }

        moviesViewModel.fetchMovies()

        return root
    }

}