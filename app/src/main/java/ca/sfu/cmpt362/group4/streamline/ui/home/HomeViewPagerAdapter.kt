package ca.sfu.cmpt362.group4.streamline.ui.home

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class HomeViewPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeMoviesFragment()
            1 -> HomeTvShowsFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }
}