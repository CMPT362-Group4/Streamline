package ca.sfu.cmpt362.group4.streamline.ui.explore

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.data_models.User
import ca.sfu.cmpt362.group4.streamline.databinding.FragmentExploreBinding
import ca.sfu.cmpt362.group4.streamline.ui.user_list_display.UserListDisplayFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ExploreFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var searchResultsRecyclerView: RecyclerView

    private val searchResultsAdapter = SearchResultsAdapter()

    private lateinit var binding: FragmentExploreBinding

    private val usersReference = FirebaseDatabase.getInstance().reference.child("users")

    private lateinit var callback: OnBackPressedCallback

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root = binding.root

        searchView = binding.searchView
        searchResultsRecyclerView = binding.searchResultsRecyclerView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Perform the search when the user submits the query
                query?.let {
                    searchUsers(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    if (it.isNotEmpty()) {
                        searchResultsRecyclerView.visibility = View.VISIBLE
                        searchUsers(it)
                    } else {
                        // Clear the search results if the query is empty
                        searchResultsAdapter.submitList(emptyList())
                    }
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            // Clear the text in the SearchView
            searchView.setQuery("", false)
            hideKeyboard()
            searchResultsRecyclerView.visibility = View.GONE
            true
        }

        setupSearchResultsRecyclerView()

        // Implement search functionality, add friend button, and friend list handling

        callback = object : OnBackPressedCallback(true /* enabled by default */) {
            override fun handleOnBackPressed() {
                // Check if the keyboard is currently visible
                val imm =
                    requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                if (imm.isAcceptingText) {
                    // If the keyboard is visible, hide the RecyclerView
                    searchResultsRecyclerView.visibility = View.GONE
                } else {
                    // If the keyboard is not visible, allow the default back button behavior
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
        }
        return root
    }


    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchView.windowToken, 0)
    }


    private fun searchUsers(query: String) {
        // Perform a Firebase query to search for users by email
        usersReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val users = mutableListOf<User>()
                for (userSnapshot in snapshot.children) {
                    val email = userSnapshot.child("email").getValue(String::class.java)
                    val uid = userSnapshot.key
                    val name = userSnapshot.child("name").getValue(String::class.java)
                    Log.d("ExploreFrag", "name is $name, email is $email, uid is $uid")

                    // Check if the name matches the query
                    if (name != null && name.contains(query, ignoreCase = true)) {
                        users.add(User(uid.orEmpty(), name, email.orEmpty()))
                    }
                }
                searchResultsAdapter.submitList(users)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun setupSearchResultsRecyclerView() {
        searchResultsRecyclerView.adapter = searchResultsAdapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(context)

        val dividerItemDecoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        val dividerDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.divider)
        dividerItemDecoration.setDrawable(dividerDrawable!!)
        searchResultsRecyclerView.addItemDecoration(dividerItemDecoration)

        // Implement click listener for search results
        searchResultsAdapter.setOnItemClickListener { user ->
            // Open HomeMoviesFragment for the selected user
            // Pass the user's UID to HomeMoviesFragment
            searchResultsRecyclerView.visibility = View.GONE
            hideKeyboard()
            searchView.clearFocus()
            val selectedUserId = user.uid
            openUserListDisplayFragment(selectedUserId)
        }
    }


    private fun openUserListDisplayFragment(userId: String) {
        Log.d("ExploreFragment", "In open function")
        // Open HomeMoviesFragment with the selected user's UID
        val bundle = Bundle()
        bundle.putString("userId", userId)

        val userListDisplayFragment = UserListDisplayFragment()
        userListDisplayFragment.arguments = bundle

        val fragmentManager = requireActivity().supportFragmentManager

        fragmentManager.addOnBackStackChangedListener {
            Log.d("BackStackChanged", "BackStack changed. Count: ${fragmentManager.backStackEntryCount}")
        }

        fragmentManager.beginTransaction()
            .replace(R.id.fragment_container, userListDisplayFragment)
            .addToBackStack(null)
            .commit()

    }
}