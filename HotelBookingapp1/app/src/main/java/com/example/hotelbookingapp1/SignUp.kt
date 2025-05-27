package com.example.hotelbookingapp1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var etName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var btnSignUp: androidx.appcompat.widget.AppCompatButton
    private lateinit var tvSignIn: android.widget.TextView

    private lateinit var multiUserPreferencesManager: MultiUserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        multiUserPreferencesManager = MultiUserPreferencesManager(this)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnSignUp = findViewById(R.id.btn_sign_up)
        tvSignIn = findViewById(R.id.tv_sign_in)
    }

    private fun setupClickListeners() {
        btnSignUp.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                performSignUp(name, email, password)
            }
        }

        tvSignIn.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        return when {
            name.isEmpty() -> {
                showToast("Please enter your name")
                false
            }
            email.isEmpty() -> {
                showToast("Please enter email")
                false
            }
            password.isEmpty() -> {
                showToast("Please enter password")
                false
            }
            confirmPassword.isEmpty() -> {
                showToast("Please confirm password")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Please enter valid email")
                false
            }
            password.length < 6 -> {
                showToast("Password must be at least 6 characters")
                false
            }
            password != confirmPassword -> {
                showToast("Passwords do not match")
                false
            }
            else -> true
        }
    }

    private fun performSignUp(name: String, email: String, password: String) {
        btnSignUp.isEnabled = false
        btnSignUp.text = "Creating account..."

        lifecycleScope.launch {
            // Check if user already exists
            if (multiUserPreferencesManager.userExists(email)) {
                showToast("User already exists with this email")
                btnSignUp.isEnabled = true
                btnSignUp.text = "Sign up"
                return@launch
            }

            // Save user data
            val success = multiUserPreferencesManager.saveUserData(name, email, password)

            if (success) {
                showToast("Account created successfully!")
                navigateToMainActivity()
            } else {
                showToast("Failed to create account")
                btnSignUp.isEnabled = true
                btnSignUp.text = "Sign up"
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}