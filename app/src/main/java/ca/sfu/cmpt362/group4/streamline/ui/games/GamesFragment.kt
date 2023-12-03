package ca.sfu.cmpt362.group4.streamline.ui.games

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentGamesBinding
import ca.sfu.cmpt362.group4.streamline.repositories.SteamRepository
import ca.sfu.cmpt362.group4.streamline.ui.movies.MovieDetailActivity
import ca.sfu.cmpt362.group4.streamline.view_models.GamesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.GamesViewModelFactory

class GamesFragment : Fragment() {
    private lateinit var binding: FragmentGamesBinding
    private lateinit var gamesAdapter: GamesAdapter
    private lateinit var gamesViewModel: GamesViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentGamesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        gamesAdapter = GamesAdapter(emptyList()) { game ->
            val intent = Intent(context, MovieDetailActivity::class.java).apply {
                putExtra("game", game)
            }
            startActivity(intent)
        }

        binding.recyclerViewGames.adapter = gamesAdapter
        binding.recyclerViewGames.layoutManager = GridLayoutManager(context, 1)

        val steamRepository = SteamRepository()
        gamesViewModel = ViewModelProvider(this, GamesViewModelFactory(steamRepository)).get(
            GamesViewModel::class.java
        )

        gamesViewModel.games.observe(viewLifecycleOwner) { games ->
            if (games != null) {
                gamesAdapter.updateGames(games)
            } else {
                Log.e("GameFragment", "Failed to update adapter")
            }
        }

        gamesViewModel.fetchGames()

        return root
    }
}