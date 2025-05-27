package com.example.hotelbookingapp1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SignInActivity : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnSignIn: androidx.appcompat.widget.AppCompatButton
    private lateinit var tvSignUp: android.widget.TextView

    private lateinit var multiUserPreferencesManager: MultiUserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        multiUserPreferencesManager = MultiUserPreferencesManager(this)

        initViews()
        setupClickListeners()
        checkLoginStatus()
    }

    private fun initViews() {
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnSignIn = findViewById(R.id.btn_sign_in)
        tvSignUp = findViewById(R.id.tv_sign_up)
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            // Add delay to ensure any pending logout completes
            delay(500)
            if (multiUserPreferencesManager.isAnyUserLoggedIn()) {
                navigateToMainActivity()
            }
        }
    }

    private fun setupClickListeners() {
        btnSignIn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (validateInputs(email, password)) {
                performSignIn(email, password)
            }
        }

        tvSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        return when {
            email.isEmpty() -> {
                showToast("Please enter email")
                false
            }
            password.isEmpty() -> {
                showToast("Please enter password")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                showToast("Please enter valid email")
                false
            }
            else -> true
        }
    }

    private fun performSignIn(email: String, password: String) {
        btnSignIn.isEnabled = false
        btnSignIn.text = "Signing in..."

        lifecycleScope.launch {
            val isValid = multiUserPreferencesManager.validateLogin(email, password)

            if (isValid) {
                showToast("Login successful!")
                navigateToMainActivity()
            } else {
                showToast("Invalid email or password")
                btnSignIn.isEnabled = true
                btnSignIn.text = "Sign in"
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}