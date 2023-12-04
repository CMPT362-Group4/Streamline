package ca.sfu.cmpt362.group4.streamline

import android.net.Uri
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.Toast
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityMainBinding
import ca.sfu.cmpt362.group4.streamline.login.LoginActivity
import ca.sfu.cmpt362.group4.streamline.profile.ProfileActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)


        //configure app bar
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_movies, R.id.nav_tv_shows,  R.id.nav_games, R.id.nav_books, R.id.nav_shared
            ), drawerLayout
        )



        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        //set up logo
        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.logo_wrapper)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.menu_hamburger)

        //navigate to respective fragment
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            if(navController.currentDestination?.id != menuItem.itemId) {
                navController.navigate(menuItem.itemId)
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val uri: Uri? = intent.data
        if (uri!= null) {
            val params: List<String> = uri.pathSegments
            val id:String = params[params.size-1]
            if (id=="share") {
                navController.navigate(R.id.nav_shared)
            }
        }


        //nav header
        val navHeader = binding.navView.getHeaderView(0)
        val userProfile = navHeader.findViewById<TextView>(R.id.profile)
        val userImage = navHeader.findViewById<ImageView>(R.id.image)

        userProfile.setOnClickListener {
            // Open ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        val currentUser = FirebaseAuth.getInstance().currentUser
        val userReference =
            currentUser?.let { FirebaseDatabase.getInstance().reference.child("users").child(it.uid) }

        // Create a ValueEventListener to listen for changes in the user's name
        val nameValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Get the updated name from Firebase
                    val updatedName = dataSnapshot.child("name").value.toString()

                    // Update the profile text view in the nav header
                    userProfile.text = updatedName
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        }

        // Add the ValueEventListener to the userReference
        userReference?.addValueEventListener(nameValueEventListener)

        userImage.setOnClickListener {
            // Open ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}