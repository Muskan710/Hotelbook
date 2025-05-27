package com.example.hotelbookingapp1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class SearchResultsAdapter(
    private val hotels: List<Hotel>,
    private val onHotelClick: (Hotel) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.SearchViewHolder>() {

    private var fragment: Fragment? = null
    private var favoritesManager: FavoritesManager? = null
    private var userPreferencesManager: MultiUserPreferencesManager? = null

    fun setFragment(fragment: Fragment) {
        this.fragment = fragment
        this.favoritesManager = FavoritesManager(fragment.requireContext())
        this.userPreferencesManager = MultiUserPreferencesManager(fragment.requireContext())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_result, parent, false)
        return SearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(hotels[position])
    }

    override fun getItemCount(): Int = hotels.size

    inner class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivHotelImage: ImageView = itemView.findViewById(R.id.ivHotelImage)
        private val tvHotelName: TextView = itemView.findViewById(R.id.tvHotelName)
        private val tvHotelLocation: TextView = itemView.findViewById(R.id.tvHotelLocation)
        private val tvHotelPrice: TextView = itemView.findViewById(R.id.tvHotelPrice)
        private val tvHotelRating: TextView = itemView.findViewById(R.id.tvHotelRating)
        private val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)

        fun bind(hotel: Hotel) {
            ivHotelImage.setImageResource(hotel.imageRes)
            tvHotelName.text = hotel.name
            tvHotelLocation.text = hotel.location
            tvHotelPrice.text = "$${hotel.price}/night"
            tvHotelRating.text = hotel.rating.toString()

            // Load favorite status from preferences
            loadFavoriteStatus(hotel)

            // Set content description for accessibility
            updateContentDescriptions(hotel)

            itemView.setOnClickListener {
                onHotelClick(hotel)
            }

            ivFavorite.setOnClickListener {
                toggleFavorite(hotel)
            }
        }

        private fun loadFavoriteStatus(hotel: Hotel) {
            fragment?.lifecycleScope?.launch {
                try {
                    val currentUser = userPreferencesManager?.getCurrentUser()
                    if (currentUser != null) {
                        val isFavorite = favoritesManager?.isFavorite(currentUser.email, hotel.id) ?: false
                        hotel.isFavorite = isFavorite

                        // Update UI on main thread
                        updateFavoriteIcon(hotel)
                        updateContentDescriptions(hotel)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        private fun toggleFavorite(hotel: Hotel) {
            fragment?.lifecycleScope?.launch {
                try {
                    val currentUser = userPreferencesManager?.getCurrentUser()
                    if (currentUser != null) {
                        val success = favoritesManager?.toggleFavorite(currentUser.email, hotel.id) ?: false

                        if (success) {
                            // Update local state
                            hotel.isFavorite = !hotel.isFavorite

                            // Update UI
                            updateFavoriteIcon(hotel)
                            updateContentDescriptions(hotel)

                            // Announce change for accessibility
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

        private fun updateFavoriteIcon(hotel: Hotel) {
            ivFavorite.setImageResource(
                if (hotel.isFavorite) R.drawable.ic_favorite_filled
                else R.drawable.ic_favorite_outline
            )
        }

        private fun updateContentDescriptions(hotel: Hotel) {
            val favoriteStatus = if (hotel.isFavorite) "favorited" else "not favorited"
            itemView.contentDescription = "${hotel.name}, ${hotel.location}, ${hotel.price} per night, ${hotel.rating} star rating, $favoriteStatus"

            ivFavorite.contentDescription = if (hotel.isFavorite) {
                "Remove ${hotel.name} from favorites"
            } else {
                "Add ${hotel.name} to favorites"
            }
        }
    }
}