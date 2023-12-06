package ca.sfu.cmpt362.group4.streamline

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ca.sfu.cmpt362.group4.streamline.login.LoginActivity
import com.google.firebase.FirebaseApp

class SplashActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Initialize Firebase
        FirebaseApp.initializeApp(this)

        Handler().postDelayed({
            // Check if the user is already logged in
            val isLoggedIn = checkUserLoggedIn()

            // Decide which activity to start based on the login status
            val targetActivity = if (isLoggedIn) MainActivity::class.java else LoginActivity::class.java

            val intent = Intent(this, targetActivity)
            startActivity(intent)
            finish()
        }, SPLASH_TIME_OUT)
    }

    private fun checkUserLoggedIn(): Boolean {
        // Check if the user is logged in by reading from SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        Log.d("Splash activity", "isLoggedIn: ${sharedPreferences.getBoolean("isLoggedIn", true)}")
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
}