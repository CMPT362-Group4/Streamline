package ca.sfu.cmpt362.group4.streamline.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import ca.sfu.cmpt362.group4.streamline.MainActivity
import ca.sfu.cmpt362.group4.streamline.R
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private var auth = FirebaseAuth.getInstance()

    private lateinit var sharedPreferences: SharedPreferences

    // Your login function
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

        auth = FirebaseAuth.getInstance()

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)


        val currentUser = auth.currentUser
        if (currentUser != null) {
            // If a user has already signed in before, fill in the email field
            binding.emailLogin.setText(currentUser.email)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.emailLogin.text.toString().trim()
            val password = binding.passwordLogin.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Sign in with email and password
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Login successful, update UI or navigate to the next screen
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()

                        // Save the login status to SharedPreferences
                        saveLoginStatus(true)

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // If login fails, display a message to the user.
                        Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // If the user doesn't have an account, they can go to the registration activity
        binding.toRegister.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }

        handleForgotPasswordButton()
    }

    private fun saveLoginStatus(isLoggedIn: Boolean) {
        // Save the login status to SharedPreferences
        sharedPreferences.edit {
            putBoolean("isLoggedIn", isLoggedIn)
        }
    }

    private fun handleForgotPasswordButton(){
        binding.btnForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }
    }

    private fun showForgotPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setView(dialogView)

        val emailEditText = dialogView.findViewById<EditText>(R.id.editEmail)

        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton("Send Reset Email") { _, _ ->
                val email = emailEditText.text.toString()
                sendPasswordResetEmail(email)
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent to $email", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email. Check your email address.", Toast.LENGTH_SHORT).show()
                }
            }
    }

}