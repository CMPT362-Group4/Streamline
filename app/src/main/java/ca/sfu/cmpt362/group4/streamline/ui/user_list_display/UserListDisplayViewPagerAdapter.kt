package ca.sfu.cmpt362.group4.streamline.ui.user_list_display

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class UserListDisplayViewPagerAdapter(fragment: Fragment, private val userId: String) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> UserListDisplayMoviesFragment.newInstance(userId)
            1 -> UserListDisplayTvShowsFragment.newInstance(userId)
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}