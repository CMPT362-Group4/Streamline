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
import ca.sfu.cmpt362.group4.streamline.data_models.TvShow
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentHomeTvShowsBinding
import ca.sfu.cmpt362.group4.streamline.ui.tv_shows.TvShowDetailActivity
import ca.sfu.cmpt362.group4.streamline.ui.tv_shows.TvShowsAdapter
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.TvShowsViewModelFactory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserListDisplayTvShowsFragment: Fragment(){

    companion object {
        fun newInstance(userId: String): UserListDisplayTvShowsFragment {
            val fragment = UserListDisplayTvShowsFragment()
            val bundle = Bundle()
            bundle.putString("userId", userId)
            fragment.arguments = bundle
            return fragment
        }
    }

    private lateinit var binding: FragmentHomeTvShowsBinding
    private lateinit var tvShowsAdapter: TvShowsAdapter

    private var uid: String? = null

    val tvShowsViewModel: TvShowsViewModel by viewModels {
        TvShowsViewModelFactory(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeTvShowsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val arguments = arguments
        uid = arguments?.getString("userId")

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
        /*tvShowsViewModel.savedTvShows.observe(viewLifecycleOwner) { savedTvShows ->
            if (savedTvShows != null) {
                tvShowsAdapter.updateTvShows(savedTvShows)
                savedTvShows.forEach { tvShows ->
                    Log.d("HomeTvShowsFragment", "Saved TvShows: ${tvShows.roomId}, ${tvShows.id}, ${tvShows.name}")

                }
            } else {
                Log.e("HomeTvShowsFragment", "Failed to update adapter")
            }
        }*/

        retrieveTvShowsFromFirebase()

        return root
    }

    private fun retrieveTvShowsFromFirebase() {
        // Assuming the 'movies' node is under the user's UID in the database

        // Retrieve data from Firebase
        uid?.let {
            FirebaseDatabase.getInstance().reference
                .child("users")
                .child(it)
                .child("tv_shows")
        }?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val tvShowsList = mutableListOf<TvShow>()

                for (movieSnapshot in dataSnapshot.children) {
                    // Parse movie data and add to the list
                    val tvShow = movieSnapshot.getValue(TvShow::class.java)

                    if (tvShow != null) {
                        tvShowsList.add(tvShow)
                    }
                }

                // Update the adapter with the retrieved movies
                tvShowsAdapter.updateTvShows(tvShowsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors, if any
                Log.e("Firebase", "Error retrieving movies from Firebase: $databaseError")
                Toast.makeText(requireContext(), "Error retrieving tv shows ", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}