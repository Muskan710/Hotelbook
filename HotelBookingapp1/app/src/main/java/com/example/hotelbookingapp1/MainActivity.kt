package com.example.hotelbookingapp1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var multiUserPreferencesManager: MultiUserPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        multiUserPreferencesManager = MultiUserPreferencesManager(this)
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // Check login status first
        checkLoginStatus()

        // Set default fragment
        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        setupBottomNavigation()
    }

    private fun checkLoginStatus() {
        lifecycleScope.launch {
            if (!multiUserPreferencesManager.isAnyUserLoggedIn()) {
                navigateToSignIn()
                return@launch
            }
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_search -> {
                    loadFragment(SearchFragment())
                    true
                }
                R.id.nav_favorites -> {
                    loadFragment(FavoritesFragment())
                    true
                }
                R.id.nav_bookings -> {
                    loadFragment(BookingsFragment())
                    true
                }
                R.id.nav_profile -> {
                    loadFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun navigateToSignIn() {
        startActivity(Intent(this, SignInActivity::class.java))
        finish()
    }

    // Method to get multi-user preferences manager for fragments
    fun getMultiUserPreferencesManager(): MultiUserPreferencesManager {
        return multiUserPreferencesManager
    }

    // Method to get current user info (convenience method for fragments)
    fun getCurrentUser(callback: (User?) -> Unit) {
        lifecycleScope.launch {
            val user = multiUserPreferencesManager.getCurrentUser()
            callback(user)
        }
    }

    // Method to get current user name (convenience method for fragments)
    fun getCurrentUserName(callback: (String) -> Unit) {
        lifecycleScope.launch {
            val name = multiUserPreferencesManager.getCurrentUserName()
            callback(name)
        }
    }

    // Method to get current user email (convenience method for fragments)
    fun getCurrentUserEmail(callback: (String) -> Unit) {
        lifecycleScope.launch {
            val email = multiUserPreferencesManager.getCurrentUserEmail()
            callback(email)
        }
    }

    // Method to logout (can be called from ProfileFragment)
    fun performLogout() {
        lifecycleScope.launch {
            val success = multiUserPreferencesManager.logout()
            if (success) {
                navigateToSignIn()
            }
        }
    }

    // Method to switch user (useful for multi-user scenarios)
    fun switchUser(email: String, callback: (Boolean) -> Unit) {
        lifecycleScope.launch {
            val success = multiUserPreferencesManager.switchUser(email)
            callback(success)
            if (success) {
                // Refresh the current fragment to show new user data
                recreate()
            }
        }
    }

    // Method to get all users (for user switching functionality)
    fun getAllUsers(callback: (List<User>) -> Unit) {
        lifecycleScope.launch {
            val users = multiUserPreferencesManager.getAllUsers()
            callback(users)
        }
    }

    // Method to update current user info
    fun updateCurrentUser(newName: String? = null, newPassword: String? = null, callback: (Boolean) -> Unit) {
        lifecycleScope.launch {
            val currentUser = multiUserPreferencesManager.getCurrentUser()
            if (currentUser != null) {
                val success = multiUserPreferencesManager.updateUser(
                    email = currentUser.email,
                    newName = newName,
                    newPassword = newPassword
                )
                callback(success)
            } else {
                callback(false)
            }
        }
    }

    // Method to check if user exists (useful for sign-up validation)
    fun checkUserExists(email: String, callback: (Boolean) -> Unit) {
        lifecycleScope.launch {
            val exists = multiUserPreferencesManager.userExists(email)
            callback(exists)
        }
    }
}