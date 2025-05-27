package com.example.hotelbookingapp1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class FavoritesAdapter(
    private val hotels: MutableList<Hotel>,
    private val onHotelClick: (Hotel) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder>() {

    private var fragment: Fragment? = null
    private var favoritesManager: FavoritesManager? = null
    private var userPreferencesManager: MultiUserPreferencesManager? = null
    private var onEmptyStateCallback: (() -> Unit)? = null

    fun setFragment(fragment: Fragment) {
        this.fragment = fragment
        this.favoritesManager = FavoritesManager(fragment.requireContext())
        this.userPreferencesManager = MultiUserPreferencesManager(fragment.requireContext())
    }

    fun setOnEmptyStateCallback(callback: () -> Unit) {
        this.onEmptyStateCallback = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_favorite_hotel, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        holder.bind(hotels[position])
    }

    override fun getItemCount(): Int = hotels.size

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivHotelImage: ImageView = itemView.findViewById(R.id.ivHotelImage)
        private val tvHotelName: TextView = itemView.findViewById(R.id.tvHotelName)
        private val tvHotelLocation: TextView = itemView.findViewById(R.id.tvHotelLocation)
        private val tvHotelPrice: TextView = itemView.findViewById(R.id.tvHotelPrice)
        private val tvHotelRating: TextView = itemView.findViewById(R.id.tvHotelRating)
        private val ivRemoveFavorite: ImageView = itemView.findViewById(R.id.ivRemoveFavorite)

        fun bind(hotel: Hotel) {
            ivHotelImage.setImageResource(hotel.imageRes)
            tvHotelName.text = hotel.name
            tvHotelLocation.text = hotel.location
            tvHotelPrice.text = "$${hotel.price}/night"
            tvHotelRating.text = hotel.rating.toString()

            // Set content descriptions for accessibility
            ivHotelImage.contentDescription = "Image of ${hotel.name}"
            tvHotelRating.contentDescription = "${hotel.rating} star rating"
            itemView.contentDescription = "${hotel.name}, ${hotel.location}, ${hotel.price} per night, ${hotel.rating} star rating"
            ivRemoveFavorite.contentDescription = "Remove ${hotel.name} from favorites"

            // Hotel item click listener
            itemView.setOnClickListener {
                onHotelClick(hotel)
            }

            // Remove from favorites click listener
            ivRemoveFavorite.setOnClickListener {
                removeFromFavorites(hotel, adapterPosition)
            }
        }

        private fun removeFromFavorites(hotel: Hotel, position: Int) {
            // Check if position is valid
            if (position == RecyclerView.NO_POSITION || position >= hotels.size) {
                return
            }

            fragment?.lifecycleScope?.launch {
                try {
                    val currentUser = userPreferencesManager?.getCurrentUser()
                    if (currentUser != null) {
                        // Use toggleFavorite instead of removeFromFavorites for consistency
                        val success = favoritesManager?.toggleFavorite(currentUser.email, hotel.id) ?: false

                        if (success) {
                            // Remove from local list and update adapter
                            hotels.removeAt(position)
                            notifyItemRemoved(position)

                            // Update the hotel's favorite status in the data manager
                            hotel.isFavorite = false

                            // Show feedback to user
                            Toast.makeText(
                                fragment?.requireContext(),
                                "${hotel.name} removed from favorites",
                                Toast.LENGTH_SHORT
                            ).show()

                            // Announce for accessibility
                            ivRemoveFavorite.announceForAccessibility("${hotel.name} removed from favorites")

                            // Check if list is empty and trigger callback
                            if (hotels.isEmpty()) {
                                onEmptyStateCallback?.invoke()
                            }
                        } else {
                            // Show error message
                            Toast.makeText(
                                fragment?.requireContext(),
                                "Failed to remove from favorites",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            fragment?.requireContext(),
                            "Please log in to manage favorites",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        fragment?.requireContext(),
                        "Error removing from favorites",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}