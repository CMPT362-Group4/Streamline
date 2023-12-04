package ca.sfu.cmpt362.group4.streamline.ui.tv_shows

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentTvShowsBinding
import ca.sfu.cmpt362.group4.streamline.repositories.TvShowsRepository
import ca.sfu.cmpt362.group4.streamline.room.DAOs.TvShowsDao
import ca.sfu.cmpt362.group4.streamline.room.databases.TvShowsDatabase
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModelFactory

class TvShowsFragment : Fragment() {
    private lateinit var binding: FragmentTvShowsBinding
    private lateinit var tvShowsAdapter: TvShowsAdapter

    private lateinit var tvShowsViewModel: TvShowsViewModel
    private lateinit var tvShowsDao: TvShowsDao
    private lateinit var tvShowsRepository: TvShowsRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentTvShowsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //initialize database
        tvShowsDao = TvShowsDatabase.getInstance(requireContext()).tvShowsDao
        tvShowsRepository = TvShowsRepository(tvShowsDao)
        tvShowsViewModel = ViewModelProvider(this, TvShowsViewModelFactory(tvShowsRepository))[TvShowsViewModel::class.java]

        //on movie click
        tvShowsAdapter = TvShowsAdapter(emptyList(), { tvShows ->
            val intent = Intent(context, TvShowDetailActivity::class.java).apply {
                putExtra("tvShows", tvShows)
                putExtra("previousPageTitle", "Movies")
            }
            startActivity(intent)
        }, R.layout.item_tv_show)

        binding.recyclerViewMovies.adapter = tvShowsAdapter
        binding.recyclerViewMovies.layoutManager = GridLayoutManager(context, 2)


        tvShowsViewModel.tvShows.observe(viewLifecycleOwner) { tvshow ->
            if(tvshow != null){
                tvShowsAdapter.updateTvShows(tvshow)
            } else {
                Log.e("TvShowsFragment", "Failed to update adapter")
            }
        }

        //fetch tvShows from api service in background
        tvShowsViewModel.fetchTvShows()


        return root
    }
}