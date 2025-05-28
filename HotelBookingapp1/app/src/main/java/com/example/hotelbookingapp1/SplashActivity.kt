package com.example.hotelbookingapp1

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    private val splashTimeOut: Long = 3000 // 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install the splash screen (Android 12+)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Hide the action bar
        supportActionBar?.hide()

        // Make it full screen
        window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                )

        // Initialize views and animations
        initializeViews()

        // Navigate to main activity after delay
        Handler(Looper.getMainLooper()).postDelayed({
            startMainActivity()
        }, splashTimeOut)
    }

    private fun initializeViews() {
        // Find views
        val logoImageView = findViewById<android.widget.ImageView>(R.id.iv_logo)

        val progressBar = findViewById<android.widget.ProgressBar>(R.id.progress_bar)

        // Load animations
        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)

        // Apply animations with delays
        Handler(Looper.getMainLooper()).postDelayed({
            logoImageView.startAnimation(fadeIn)
            logoImageView.visibility = View.VISIBLE
        }, 500)

        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.VISIBLE
        }, 2000)
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Add transition animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        // Finish splash activity
        finish()
    }
}
