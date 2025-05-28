package com.example.hotelbookingapp1

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HotelDetailActivity : AppCompatActivity() {

    private lateinit var ivHotelImage: ImageView
    private lateinit var ivFavorite: ImageView
    private lateinit var tvHotelName: TextView
    private lateinit var tvLocation: TextView
    private lateinit var tvPrice: TextView
    private lateinit var tvRating: TextView
    private lateinit var tvDescription: TextView
    private lateinit var btnBookingNow: Button
    private lateinit var ivBack: ImageView

    private lateinit var favoritesManager: FavoritesManager
    private lateinit var userPreferencesManager: MultiUserPreferencesManager
    private lateinit var bookingsManager: BookingsManager

    private var currentHotel: Hotel? = null
    private var selectedDate: String? = null

    companion object {
        private const val EXTRA_HOTEL_ID = "extra_hotel_id"

        fun createIntent(context: Context, hotelId: Int): Intent {
            return Intent(context, HotelDetailActivity::class.java).apply {
                putExtra(EXTRA_HOTEL_ID, hotelId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hotel_detail)

        initViews()
        initManagers()
        setupClickListeners()
        loadHotelDetails()
    }

    private fun initViews() {
        ivHotelImage = findViewById(R.id.ivHotelImage)
        ivFavorite = findViewById(R.id.ivFavorite)
        tvHotelName = findViewById(R.id.tvHotelName)
        tvLocation = findViewById(R.id.tvLocation)
        tvPrice = findViewById(R.id.tvPrice)
        tvRating = findViewById(R.id.tvRating)
        tvDescription = findViewById(R.id.tvDescription)
        btnBookingNow = findViewById(R.id.btnBookingNow)
        ivBack = findViewById(R.id.ivBack)
    }

    private fun initManagers() {
        favoritesManager = FavoritesManager(this)
        userPreferencesManager = MultiUserPreferencesManager(this)
        bookingsManager = BookingsManager(this)
    }

    private fun setupClickListeners() {
        ivBack.setOnClickListener {
            onBackPressed()
        }

        ivFavorite.setOnClickListener {
            toggleFavorite()
        }

        btnBookingNow.setOnClickListener {
            showBookingDialog()
        }
    }

    private fun loadHotelDetails() {
        val hotelId = intent.getIntExtra(EXTRA_HOTEL_ID, -1)

        if (hotelId == -1) {
            finish()
            return
        }

        // Get hotel from data manager
        currentHotel = HotelDataManager.getHotelById(hotelId)

        if (currentHotel == null) {
            finish()
            return
        }

        displayHotelDetails(currentHotel!!)
        loadFavoriteStatus()
    }

    private fun displayHotelDetails(hotel: Hotel) {
        ivHotelImage.setImageResource(hotel.imageRes)
        tvHotelName.text = hotel.name
        tvLocation.text = hotel.location
        tvPrice.text = "$${hotel.price}"
        tvRating.text = hotel.rating.toString()

        // Set description - you might want to add description field to Hotel class
        tvDescription.text = getHotelDescription(hotel)

        // Set content descriptions for accessibility
        ivHotelImage.contentDescription = "Image of ${hotel.name}"
        tvRating.contentDescription = "${hotel.rating} star rating"
        btnBookingNow.contentDescription = "Book ${hotel.name} now"
    }

    private fun getHotelDescription(hotel: Hotel): String {
        // You can create a more sophisticated description system
        // For now, using a simple template
        return "${hotel.name}, ${hotel.location} is a modern hotel: elegant ${hotel.rating} star hotel overlooking the sea. perfect for a romantic, charming experience with excellent amenities and services."
    }

    private fun loadFavoriteStatus() {
        currentHotel?.let { hotel ->
            lifecycleScope.launch {
                try {
                    val currentUser = userPreferencesManager.getCurrentUser()
                    if (currentUser != null) {
                        val isFavorite = favoritesManager.isFavorite(currentUser.email, hotel.id)
                        hotel.isFavorite = isFavorite
                        updateFavoriteIcon(hotel)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun toggleFavorite() {
        currentHotel?.let { hotel ->
            lifecycleScope.launch {
                try {
                    val currentUser = userPreferencesManager.getCurrentUser()
                    if (currentUser != null) {
                        val success = favoritesManager.toggleFavorite(currentUser.email, hotel.id)

                        if (success) {
                            hotel.isFavorite = !hotel.isFavorite
                            updateFavoriteIcon(hotel)

                            val announcement = if (hotel.isFavorite) {
                                "${hotel.name} added to favorites"
                            } else {
                                "${hotel.name} removed from favorites"
                            }
                            ivFavorite.announceForAccessibility(announcement)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun updateFavoriteIcon(hotel: Hotel) {
        ivFavorite.setImageResource(
            if (hotel.isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_outline
        )

        ivFavorite.contentDescription = if (hotel.isFavorite) {
            "Remove ${hotel.name} from favorites"
        } else {
            "Add ${hotel.name} to favorites"
        }
    }

    private fun showBookingDialog() {
        currentHotel?.let { hotel ->
            val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_booking, null)

            // Initialize dialog views
            val ivDialogHotelImage = dialogView.findViewById<ImageView>(R.id.ivDialogHotelImage)
            val tvDialogHotelName = dialogView.findViewById<TextView>(R.id.tvDialogHotelName)
            val tvDialogHotelLocation = dialogView.findViewById<TextView>(R.id.tvDialogHotelLocation)
            val tvDialogHotelPrice = dialogView.findViewById<TextView>(R.id.tvDialogHotelPrice)
            val btnSelectDate = dialogView.findViewById<Button>(R.id.btnSelectDate)
            val tvSelectedDate = dialogView.findViewById<TextView>(R.id.tvSelectedDate)
            val btnCancelBooking = dialogView.findViewById<Button>(R.id.btnCancelBooking)
            val btnConfirmBooking = dialogView.findViewById<Button>(R.id.btnConfirmBooking)

            // Set hotel information
            ivDialogHotelImage.setImageResource(hotel.imageRes)
            tvDialogHotelName.text = hotel.name
            tvDialogHotelLocation.text = hotel.location
            tvDialogHotelPrice.text = "$${hotel.price}/night"

            // Create dialog
            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create()

            // Date selection
            btnSelectDate.setOnClickListener {
                showDatePicker { date ->
                    selectedDate = date
                    tvSelectedDate.text = "Check-in: $date"
                    tvSelectedDate.visibility = View.VISIBLE
                    btnConfirmBooking.isEnabled = true
                }
            }

            // Cancel button
            btnCancelBooking.setOnClickListener {
                dialog.dismiss()
            }

            // Confirm booking button
            btnConfirmBooking.setOnClickListener {
                selectedDate?.let { date ->
                    confirmBooking(hotel, date)
                    dialog.dismiss()
                }
            }

            dialog.show()
        }
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // Set minimum date to today
        calendar.set(currentYear, currentMonth, currentDay)
        val minDate = calendar.timeInMillis

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)

                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedCalendar.time)

                onDateSelected(formattedDate)
            },
            currentYear,
            currentMonth,
            currentDay
        )

        // Set minimum date to today
        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.show()
    }

    private fun confirmBooking(hotel: Hotel, checkInDate: String) {
        lifecycleScope.launch {
            try {
                val currentUser = userPreferencesManager.getCurrentUser()
                if (currentUser != null) {
                    val success = bookingsManager.addBooking(currentUser.email, hotel, checkInDate)

                    if (success) {
                        Toast.makeText(
                            this@HotelDetailActivity,
                            "Booking confirmed for ${hotel.name}!",
                            Toast.LENGTH_LONG
                        ).show()

                        // Optional: Show booking confirmation details
                        showBookingConfirmation(hotel, checkInDate)
                    } else {
                        Toast.makeText(
                            this@HotelDetailActivity,
                            "Failed to confirm booking. Please try again.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@HotelDetailActivity,
                        "Please log in to make a booking",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(
                    this@HotelDetailActivity,
                    "An error occurred. Please try again.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showBookingConfirmation(hotel: Hotel, checkInDate: String) {
        AlertDialog.Builder(this)
            .setTitle("Booking Confirmed!")
            .setMessage("Your booking for ${hotel.name} on $checkInDate has been confirmed.\n\nYou can view your bookings in the Bookings tab.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .setNeutralButton("View Bookings") { dialog, _ ->
                dialog.dismiss()
                // Optional: Navigate to bookings fragment
                // You can implement this based on your navigation structure
            }
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}