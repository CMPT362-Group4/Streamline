package ca.sfu.cmpt362.group4.streamline.ui.user_list_display

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentUserListDisplayBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth

class UserListDisplayFragment: Fragment(){
    private lateinit var binding: FragmentUserListDisplayBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View {

        binding = FragmentUserListDisplayBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //setHasOptionsMenu(true)

        val arguments = arguments
        val userId = arguments?.getString("userId")

        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        val adapter = UserListDisplayViewPagerAdapter(this, userId ?: "")
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Movies"
                1 -> "TV Shows"
                else -> throw IllegalArgumentException("Invalid position: $position")
            }
        }.attach()



        //add logo
        //(requireActivity() as AppCompatActivity).supportActionBar?.setDisplayUseLogoEnabled(true)


        return root
    }

}