package ca.sfu.cmpt362.group4.streamline.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.edit
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityLoginBinding
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityProfileBinding
import ca.sfu.cmpt362.group4.streamline.login.LoginActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Back button
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.back_arrow)

        // Initialize Firebase instance and database
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        databaseReference = FirebaseDatabase.getInstance().reference.child("users").child(currentUser.uid)

        // Load and display user's name if already set
        databaseReference.get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                // Load user name from Firebase
                val savedUserName = dataSnapshot.child("name").value.toString()

                if(!savedUserName.isNullOrEmpty())
                    binding.userName.setText(savedUserName)
            }
        }

        // Display email (it should be pre-filled as the user used it to register)
        binding.userEmail.setText(currentUser.email)

        // Save changes button click listener
        binding.btnSaveChanges.setOnClickListener {
            saveChanges()
            Toast.makeText(this, "Profile Saved", Toast.LENGTH_SHORT).show()
            finish()
        }

        handleLogoutButton()
        handleChangePasswordButton()
    }

    private fun saveChanges() {
        val userName = binding.userName.text.toString()

        // Update user's name in the Realtime Database
        databaseReference.child("name").setValue(userName)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Handle back button press
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun handleLogoutButton(){
        binding.btnLogout.setOnClickListener{
            saveLoginStatus(false)

            // Redirect to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        // Save the login status to SharedPreferences
        val sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit {
            putBoolean("isLoggedIn", isLoggedIn)
        }
        Log.d("Profile Activity", "isLoggedIn: ${sharedPreferences.getBoolean("isLoggedIn", true)}")
    }

    private fun handleChangePasswordButton(){
        binding.btnChangePassword.setOnClickListener{
            showChangePasswordDialog()
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(dialogView)

        val currentPasswordEditText = dialogView.findViewById<EditText>(R.id.editCurrentPassword)
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.editNewPassword)
        val confirmPasswordEditText = dialogView.findViewById<EditText>(R.id.editConfirmPassword)

        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Change") { _, _ ->
                val currentPassword = currentPasswordEditText.text.toString()
                val newPassword = newPasswordEditText.text.toString()
                val confirmPassword = confirmPasswordEditText.text.toString()

                if (newPassword == confirmPassword) {
                    changePassword(currentPassword, newPassword)
                } else {
                    Toast.makeText(this, "New passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }



    //change password on firebase
    private fun changePassword(currentPassword: String, newPassword: String) {

        val credential = EmailAuthProvider.getCredential(currentUser?.email!!, currentPassword)

        currentUser.reauthenticate(credential).addOnCompleteListener { authTask ->
            if (authTask.isSuccessful) {
                currentUser.updatePassword(newPassword)
                    .addOnCompleteListener { passwordTask ->
                        if (passwordTask.isSuccessful) {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to update password", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Authentication failed. Check your current password", Toast.LENGTH_SHORT).show()
            }
        }
    }


}