package com.example.hotelbookingapp1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class FavoritesFragment : Fragment() {

    private lateinit var rvFavorites: RecyclerView
    private lateinit var llEmptyState: LinearLayout
    private lateinit var tvEmptyMessage: TextView

    private lateinit var favoritesAdapter: FavoritesAdapter
    private lateinit var favoritesManager: FavoritesManager
    private lateinit var userPreferencesManager: MultiUserPreferencesManager

    private val favoriteHotels = mutableListOf<Hotel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        initManagers()
        setupRecyclerView()
        loadFavorites()
    }

    override fun onResume() {
        super.onResume()
        // Refresh favorites when fragment becomes visible
        loadFavorites()
    }

    private fun initViews(view: View) {
        rvFavorites = view.findViewById(R.id.rvFavorites)
        llEmptyState = view.findViewById(R.id.llEmptyState)
        tvEmptyMessage = view.findViewById(R.id.tvEmptyMessage)
    }

    private fun initManagers() {
        favoritesManager = FavoritesManager(requireContext())
        userPreferencesManager = MultiUserPreferencesManager(requireContext())
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter(favoriteHotels) { hotel ->
            onHotelClick(hotel)
        }

        // Set the fragment reference and empty state callback
        favoritesAdapter.setFragment(this)
        favoritesAdapter.setOnEmptyStateCallback {
            updateUI()
        }

        rvFavorites.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = favoritesAdapter
        }
    }

    private fun loadFavorites() {
        lifecycleScope.launch {
            try {
                val currentUser = userPreferencesManager.getCurrentUser()
                if (currentUser != null) {
                    val favorites = favoritesManager.getUserFavoriteHotels(currentUser.email)

                    favoriteHotels.clear()
                    favoriteHotels.addAll(favorites)

                    // Update UI on main thread
                    favoritesAdapter.notifyDataSetChanged()
                    updateUI()
                } else {
                    // No user logged in
                    favoriteHotels.clear()
                    favoritesAdapter.notifyDataSetChanged()
                    showEmptyState("Please log in to view your favorites")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                showEmptyState("Error loading favorites")
            }
        }
    }

    private fun updateUI() {
        if (favoriteHotels.isEmpty()) {
            showEmptyState("No favorite hotels yet")
        } else {
            showFavorites()
        }
    }

    private fun showEmptyState(message: String) {
        llEmptyState.visibility = View.VISIBLE
        rvFavorites.visibility = View.GONE
        tvEmptyMessage.text = message
    }

    private fun showFavorites() {
        llEmptyState.visibility = View.GONE
        rvFavorites.visibility = View.VISIBLE
    }

    private fun onHotelClick(hotel: Hotel) {
        // Navigate to hotel details
        val intent = HotelDetailActivity.createIntent(requireContext(), hotel.id)
        startActivity(intent)
    }

    // Method to refresh favorites from other fragments
    fun refreshFavorites() {
        loadFavorites()
    }
}