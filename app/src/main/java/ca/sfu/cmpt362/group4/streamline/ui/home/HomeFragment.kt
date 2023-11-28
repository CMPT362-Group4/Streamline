package ca.sfu.cmpt362.group4.streamline.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var addButton: FloatingActionButton
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        addButton = binding.addButton

        handleAddButton()
        return root
    }

    private fun handleAddButton(){
        addButton.setOnClickListener(){
            Log.d("Home", "button clicked")
            ChooseMediaTypeDialogFragment().show(parentFragmentManager, "chooseMediaType")
        }
    }
}