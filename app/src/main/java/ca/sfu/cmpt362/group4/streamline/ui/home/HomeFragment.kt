package ca.sfu.cmpt362.group4.streamline.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentHomeBinding
import ca.sfu.cmpt362.group4.streamline.repositories.MoviesRepository
import ca.sfu.cmpt362.group4.streamline.room.DAOs.MovieDao
import ca.sfu.cmpt362.group4.streamline.room.databases.MovieDatabase
import ca.sfu.cmpt362.group4.streamline.ui.movies.MovieDetailActivity
import ca.sfu.cmpt362.group4.streamline.ui.movies.MoviesAdapter
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.MoviesViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {
    private lateinit var addButton: FloatingActionButton



    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setHasOptionsMenu(true)

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val adapter = HomeViewPagerAdapter(this)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Movies"
                1 -> "TV Shows"
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }.attach()



        //add logo
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(true)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_delete_all -> {
                //get the right fragment i.e. homeMovies or homeTvShpw and call the correct handleDeleteAll
                val navHostFragment = requireActivity().supportFragmentManager.fragments[0] as? NavHostFragment

                navHostFragment?.let { navFragment ->
                    val homeFrag = navFragment.childFragmentManager.fragments[0]
                    val currentFragment = homeFrag.childFragmentManager.fragments[0]

                    Log.d("HomeFragment", "Current child fragment: $currentFragment")

                    if (currentFragment is DeleteAllHandler) {
                        showConfirmationDialog(currentFragment)
                    } else {
                        Log.d("HomeFragment", "Current child fragment is NOT DeleteAllHandler")
                    }
                }

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmationDialog(deleteAllHandler: DeleteAllHandler) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Confirmation")
            .setMessage("Are you sure you want to delete all entries?")
            .setPositiveButton("OK") { _, _ ->
                //Call correct deleteAll function
                deleteAllHandler.handleDeleteAll()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

}

interface DeleteAllHandler {
    fun handleDeleteAll()
}