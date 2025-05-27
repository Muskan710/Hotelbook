package com.example.hotelbookingapp1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class BookingsFragment : Fragment() {

    private lateinit var rvBookings: RecyclerView
    private lateinit var layoutEmptyBookings: View

    private lateinit var bookingsAdapter: BookingsAdapter
    private lateinit var bookingsManager: BookingsManager
    private lateinit var userPreferencesManager: MultiUserPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_bookings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initManagers()
        setupRecyclerView()
        loadBookings()
    }

    override fun onResume() {
        super.onResume()
        loadBookings()
    }

    private fun initViews(view: View) {
        rvBookings = view.findViewById(R.id.rvBookings)
        layoutEmptyBookings = view.findViewById(R.id.layoutEmptyBookings)
    }

    private fun initManagers() {
        bookingsManager = BookingsManager(requireContext())
        userPreferencesManager = MultiUserPreferencesManager(requireContext())
    }

    private fun setupRecyclerView() {
        bookingsAdapter = BookingsAdapter { booking ->
            cancelBooking(booking)
        }

        rvBookings.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookingsAdapter
        }
    }

    private fun loadBookings() {
        lifecycleScope.launch {
            try {
                val currentUser = userPreferencesManager.getCurrentUser()
                if (currentUser != null) {
                    val bookings = bookingsManager.getUserBookings(currentUser.email)
                    updateUI(bookings)
                } else {
                    updateUI(emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
                updateUI(emptyList())
            }
        }
    }

    private fun updateUI(bookings: List<Booking>) {
        if (bookings.isEmpty()) {
            rvBookings.visibility = View.GONE
            layoutEmptyBookings.visibility = View.VISIBLE
        } else {
            rvBookings.visibility = View.VISIBLE
            layoutEmptyBookings.visibility = View.GONE
            bookingsAdapter.updateBookings(bookings)
        }
    }

    private fun cancelBooking(booking: Booking) {
        lifecycleScope.launch {
            try {
                val currentUser = userPreferencesManager.getCurrentUser()
                if (currentUser != null) {
                    val success = bookingsManager.cancelBooking(currentUser.email, booking.id)
                    if (success) {
                        loadBookings() // Refresh the list
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}