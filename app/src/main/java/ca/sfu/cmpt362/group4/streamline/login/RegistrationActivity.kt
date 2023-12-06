package ca.sfu.cmpt362.group4.streamline.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ca.sfu.cmpt362.group4.streamline.MainActivity
import ca.sfu.cmpt362.group4.streamline.databinding.ActivityRegistrationBinding
import ca.sfu.cmpt362.group4.streamline.data_models.User
import ca.sfu.cmpt362.group4.streamline.view_models.UserViewModel
import ca.sfu.cmpt362.group4.streamline.view_models.UserViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class RegistrationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegistrationBinding

    private var auth = FirebaseAuth.getInstance()

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnRegister.setOnClickListener {
            val name = binding.nameRegister.text.toString().trim()
            val email = binding.emailRegister.text.toString().trim()
            val password = binding.passwordRegister.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registration successful, update UI or navigate to the next screen
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                    val userId = auth.currentUser?.uid

                    // Store user's email and name in the database under their UID
                    userId?.let {
                        userViewModel.storeUser(userId, name, email)
                    }

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // If registration fails, display a message to the user.
                    Toast.makeText(this, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // If the user already has an account, they can go to the login activity
        binding.toLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}