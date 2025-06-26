package com.example.gnm18

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.gnm18.Models.UserViewModel
import com.example.gnm18.databinding.ActivityLoginBinding

/**
 * Handles user authentication and login functionality
 * - Validates user credentials against database
 * - Stores user ID in SharedPreferences upon successful login
 * - Navigates to HomeActivity after login
 * - Provides registration navigation
 */
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.getUser(username, password).observe(this) { user ->
                if (user != null) {
                    val sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putInt("user_id", user.id)
                        apply()
                    }
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()

                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}