package com.example.hotelbookingapp1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var tvUserName: TextView
    private lateinit var tvUserEmail: TextView
    private lateinit var tvProfileName: TextView
    private lateinit var tvProfileEmail: TextView
    private lateinit var layoutEditProfile: LinearLayout
    private lateinit var layoutChangePassword: LinearLayout
    private lateinit var layoutLogout: LinearLayout

    private lateinit var preferencesManager: MultiUserPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupPreferencesManager()
        setupClickListeners()
        loadUserData()
    }

    private fun initViews(view: View) {
        tvUserName = view.findViewById(R.id.tvUserName)
        tvUserEmail = view.findViewById(R.id.tvUserEmail)
        tvProfileName = view.findViewById(R.id.tvProfileName)
        tvProfileEmail = view.findViewById(R.id.tvProfileEmail)
        layoutEditProfile = view.findViewById(R.id.layoutEditProfile)
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword)
        layoutLogout = view.findViewById(R.id.layoutLogout)
    }

    private fun setupPreferencesManager() {
        preferencesManager = MultiUserPreferencesManager(requireContext())
    }

    private fun setupClickListeners() {
        layoutEditProfile.setOnClickListener {
            showEditNameDialog()
        }

        layoutChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }

        layoutLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun loadUserData() {
        lifecycleScope.launch {
            try {
                val currentUser = preferencesManager.getCurrentUser()
                if (currentUser != null) {
                    updateUI(currentUser)
                } else {
                    redirectToLogin()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading user data", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Error loading user data", e)
            }
        }
    }

    private fun updateUI(user: User) {
        tvUserName.text = user.name
        tvUserEmail.text = user.email
        tvProfileName.text = user.name
        tvProfileEmail.text = user.email
    }

    private fun showEditNameDialog() {
        lifecycleScope.launch {
            try {
                val currentUser = preferencesManager.getCurrentUser()
                if (currentUser != null) {
                    val dialogView = LayoutInflater.from(context).inflate(android.R.layout.select_dialog_item, null)
                    val etNewName = android.widget.EditText(context).apply {
                        setText(currentUser.name)
                        hint = "Enter new name"
                        setPadding(50, 30, 50, 30)
                        selectAll()
                    }

                    AlertDialog.Builder(context)
                        .setTitle("Edit Name")
                        .setView(etNewName)
                        .setPositiveButton("Save") { _, _ ->
                            val newName = etNewName.text.toString().trim()
                            if (validateName(newName)) {
                                updateUserName(newName)
                            }
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error loading user data", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Error in edit name dialog", e)
            }
        }
    }

    private fun validateName(name: String): Boolean {
        return when {
            name.isEmpty() -> {
                Toast.makeText(context, "Please enter a name", Toast.LENGTH_SHORT).show()
                false
            }
            name.length < 2 -> {
                Toast.makeText(context, "Name must be at least 2 characters", Toast.LENGTH_SHORT).show()
                false
            }
            name.length > 50 -> {
                Toast.makeText(context, "Name must be less than 50 characters", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun updateUserName(newName: String) {
        lifecycleScope.launch {
            try {
                val currentUser = preferencesManager.getCurrentUser()
                if (currentUser != null) {
                    val success = preferencesManager.updateUser(
                        email = currentUser.email,
                        newName = newName
                    )

                    if (success) {
                        Toast.makeText(context, "Name updated successfully", Toast.LENGTH_SHORT).show()
                        loadUserData()
                    } else {
                        Toast.makeText(context, "Failed to update name", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error updating name", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Error updating name", e)
            }
        }
    }

    private fun showChangePasswordDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<android.widget.EditText>(R.id.etCurrentPassword)
        val etNewPassword = dialogView.findViewById<android.widget.EditText>(R.id.etNewPassword)
        val etConfirmPassword = dialogView.findViewById<android.widget.EditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(context)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString().trim()
                val newPassword = etNewPassword.text.toString().trim()
                val confirmPassword = etConfirmPassword.text.toString().trim()

                if (validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
                    changePassword(currentPassword, newPassword)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun validatePasswordChange(currentPassword: String, newPassword: String, confirmPassword: String): Boolean {
        return when {
            currentPassword.isEmpty() -> {
                Toast.makeText(context, "Please enter current password", Toast.LENGTH_SHORT).show()
                false
            }
            newPassword.isEmpty() -> {
                Toast.makeText(context, "Please enter new password", Toast.LENGTH_SHORT).show()
                false
            }
            newPassword.length < 6 -> {
                Toast.makeText(context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                false
            }
            newPassword != confirmPassword -> {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }

    private fun changePassword(currentPassword: String, newPassword: String) {
        lifecycleScope.launch {
            try {
                val currentUser = preferencesManager.getCurrentUser()
                if (currentUser != null) {
                    if (currentUser.password == currentPassword) {
                        val success = preferencesManager.updateUser(
                            email = currentUser.email,
                            newPassword = newPassword
                        )

                        if (success) {
                            Toast.makeText(context, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Failed to change password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error changing password", Toast.LENGTH_SHORT).show()
                Log.e("ProfileFragment", "Error changing password", e)
            }
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(context)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                performLogout()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                Log.d("ProfileFragment", "Attempting logout...")
                val success = preferencesManager.logout()
                if (success) {
                    Log.d("ProfileFragment", "Logout successful")
                    delay(300) // Small delay to ensure logout completes
                    Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                    redirectToLogin()
                } else {
                    Log.d("ProfileFragment", "Logout failed")
                    Toast.makeText(context, "Failed to logout", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Error during logout", e)
                Toast.makeText(context, "Error during logout", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun redirectToLogin() {
        val intent = Intent(context, SignInActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        startActivity(intent)
        activity?.finish()
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }
}