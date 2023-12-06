package ca.sfu.cmpt362.group4.streamline.ui.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentBooksBinding

class BooksFragment : Fragment() {
    private lateinit var binding: FragmentBooksBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentBooksBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }
}