package ca.sfu.cmpt362.group4.streamline.ui.home

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
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentHomeTvShowsBinding
import ca.sfu.cmpt362.group4.streamline.ui.tv_shows.TvShowDetailActivity
import ca.sfu.cmpt362.group4.streamline.ui.tv_shows.TvShowsAdapter
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModelFactory
import com.google.firebase.auth.FirebaseAuth

class HomeTvShowsFragment : Fragment(), DeleteAllHandler {

    private lateinit var binding: FragmentHomeTvShowsBinding
    private lateinit var tvShowsAdapter: TvShowsAdapter

    val tvShowsViewModel: TvShowsViewModel by viewModels {
        TvShowsViewModelFactory(requireContext())
    }

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeTvShowsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //handle on item click
        tvShowsAdapter = TvShowsAdapter(emptyList(), { tvShows ->
            val intent = Intent(context, TvShowDetailActivity::class.java).apply {
                putExtra("tvShows", tvShows)
                putExtra("previousPageTitle", "HomeTvShows")
            }
            startActivity(intent)
        }, R.layout.item_tv_shows_home)

        binding.recyclerViewSavedTvShows.adapter = tvShowsAdapter
        binding.recyclerViewSavedTvShows.layoutManager = LinearLayoutManager(context)

        //update tvShows list when data changed
        tvShowsViewModel.savedTvShows.observe(viewLifecycleOwner) { savedTvShows ->
            if (savedTvShows != null) {
                tvShowsAdapter.updateTvShows(savedTvShows)
                savedTvShows.forEach { tvShows ->
                    Log.d("HomeTvShowsFragment", "Saved TvShows: ${tvShows.roomId}, ${tvShows.id}, ${tvShows.name}")

                }
            } else {
                Log.e("HomeTvShowsFragment", "Failed to update adapter")
            }
        }

        return root
    }

    override fun handleDeleteAll() {
        // Implement logic to delete all tvShows using tvShowsViewModel
        tvShowsViewModel.deleteAllTvShows()
        Toast.makeText(requireContext(), "All tvShows deleted from your list", Toast.LENGTH_SHORT).show()
    }
}