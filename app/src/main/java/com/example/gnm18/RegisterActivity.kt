package com.example.gnm18

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.gnm18.Models.UserViewModel
import com.example.gnm18.RoomDb.User

/**
 * Handles new user registration
 * - Validates registration form inputs
 * - Checks for username availability
 * - Creates new user accounts in database
 * - Provides feedback during registration process
 */
class RegisterActivity : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize views
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        btnRegister.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userViewModel.checkUsernameExists(username).observe(this) { existingUser ->
                if (existingUser != null) {
                    Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
                } else {
                    val newUser = User(username = username, password = password)
                    userViewModel.insert(newUser)
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}