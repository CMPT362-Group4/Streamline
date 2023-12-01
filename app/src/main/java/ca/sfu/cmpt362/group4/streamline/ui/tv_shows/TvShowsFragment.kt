package ca.sfu.cmpt362.group4.streamline.ui.tv_shows

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.GridLayoutManager
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentTvShowsBinding
import ca.sfu.cmpt362.group4.streamline.repositories.TvShowsRepository
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModelFactory

class TvShowsFragment : Fragment() {
    private lateinit var binding: FragmentTvShowsBinding
    private lateinit var tvShowsAdapter: TvShowsAdapter
    private lateinit var tvShowsViewModel: TvShowsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentTvShowsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        tvShowsAdapter = TvShowsAdapter(emptyList()) { tvShows ->
            val intent = Intent(context, TvShowDetailActivity::class.java).apply {
                putExtra("tvShow", tvShows)
            }
            startActivity(intent)
        }

        binding.recyclerViewMovies.adapter = tvShowsAdapter
        binding.recyclerViewMovies.layoutManager = GridLayoutManager(context, 2)

        val tvShowsRepository = TvShowsRepository()
        tvShowsViewModel = ViewModelProvider(this, TvShowsViewModelFactory(tvShowsRepository)).get(TvShowsViewModel::class.java)

        tvShowsViewModel.tvShows.observe(viewLifecycleOwner) { tvshow ->
            if(tvshow != null){
                tvShowsAdapter.updateTvShows(tvshow)
            } else {
                Log.e("TvShowsFragment", "Failed to update adapter")
            }
        }
        tvShowsViewModel.fetchTvShows()


        return root
    }
}