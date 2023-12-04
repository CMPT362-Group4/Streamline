package ca.sfu.cmpt362.group4.streamline.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentHomeTvShowsBinding
import ca.sfu.cmpt362.group4.streamline.repositories.MoviesRepository
import ca.sfu.cmpt362.group4.streamline.room.DAOs.MovieDao
import ca.sfu.cmpt362.group4.streamline.ui.tv_shows.TvShowsAdapter
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModel

class HomeTvShowsFragment : Fragment(), DeleteAllHandler {

    private lateinit var binding: FragmentHomeTvShowsBinding
    private lateinit var tvShowsAdapter: TvShowsAdapter

    private lateinit var tvShowsViewModel: TvShowsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeTvShowsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun handleDeleteAll() {
        Toast.makeText(requireContext(), "All tv shows deleted from your list", Toast.LENGTH_SHORT).show()
    }
}