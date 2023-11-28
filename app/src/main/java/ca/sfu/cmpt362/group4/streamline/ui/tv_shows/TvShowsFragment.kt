package ca.sfu.cmpt362.group4.streamline.ui.tv_shows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentTvShowsBinding

class TvShowsFragment : Fragment() {
    private lateinit var binding: FragmentTvShowsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentTvShowsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }
}