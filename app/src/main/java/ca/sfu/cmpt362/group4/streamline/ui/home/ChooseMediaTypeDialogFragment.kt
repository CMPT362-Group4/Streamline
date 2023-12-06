package ca.sfu.cmpt362.group4.streamline.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.DialogFragmentChooseMediaTypeBinding

class ChooseMediaTypeDialogFragment : DialogFragment() {
    private lateinit var binding: DialogFragmentChooseMediaTypeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d("ChooseMedia", "created")
        binding = DialogFragmentChooseMediaTypeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonMovies.setOnClickListener {
            navigateToFragment(R.id.nav_movies)
        }
        binding.buttonTvShows.setOnClickListener {
            navigateToFragment(R.id.nav_tv_shows)
        }
        binding.buttonBooks.setOnClickListener {
            navigateToFragment(R.id.nav_books)
        }
        binding.buttonGames.setOnClickListener {
            navigateToFragment(R.id.nav_games)
        }


        return root
    }

    private fun navigateToFragment(fragmentId: Int) {
        val navController = findNavController()
        navController.popBackStack(R.id.nav_home, true)
        navController.navigate(fragmentId)
        dismiss()
    }
}