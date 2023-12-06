package ca.sfu.cmpt362.group4.streamline.ui.movies

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentMoviesBinding
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory
import android.content.Intent
import androidx.fragment.app.viewModels
import ca.sfu.cmpt362.group4.streamline.R
import com.google.firebase.auth.FirebaseAuth


class MoviesFragment : Fragment() {
    private lateinit var binding: FragmentMoviesBinding
    private lateinit var moviesAdapter: MoviesAdapter

    private val moviesViewModel: MoviesViewModel by viewModels {
        MoviesViewModelFactory(requireContext())
    }

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentMoviesBinding.inflate(inflater, container, false)
        val root: View = binding.root


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

        moviesViewModel.fetchMovies()

        return root
    }

}
