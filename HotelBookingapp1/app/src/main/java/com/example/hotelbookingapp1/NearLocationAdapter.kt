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

class NearLocationAdapter(
    private var hotels: MutableList<Hotel>
) : RecyclerView.Adapter<NearLocationAdapter.ViewHolder>() {

    private var fragment: Fragment? = null
    private var favoritesManager: FavoritesManager? = null
    private var userPreferencesManager: MultiUserPreferencesManager? = null

    fun setFragment(fragment: Fragment) {
        this.fragment = fragment
        this.favoritesManager = FavoritesManager(fragment.requireContext())
        this.userPreferencesManager = MultiUserPreferencesManager(fragment.requireContext())
    }

    // Method to update data when tabs are switched
    fun updateData(newHotels: List<Hotel>) {
        hotels = newHotels.toMutableList()
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivHotelImage: ImageView = itemView.findViewById(R.id.ivHotelImage)
        val ivFavorite: ImageView = itemView.findViewById(R.id.ivFavorite)
        val tvHotelName: TextView = itemView.findViewById(R.id.tvHotelName)
        val tvLocation: TextView = itemView.findViewById(R.id.tvLocation)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvRating: TextView = itemView.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hotel_horizontal, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hotel = hotels[position]

        holder.ivHotelImage.setImageResource(hotel.imageRes)
        holder.tvHotelName.text = hotel.name
        holder.tvLocation.text = hotel.location
        holder.tvPrice.text = "$${hotel.price}"
        holder.tvRating.text = hotel.rating.toString()

        // Load favorite status from preferences
        loadFavoriteStatus(hotel, holder)

        // Set content descriptions for accessibility
        updateContentDescriptions(hotel, holder)

        // Handle hotel item click - navigate to detail activity
        holder.itemView.setOnClickListener {
            onHotelClick(hotel, holder)
        }

        // Handle favorite click
        holder.ivFavorite.setOnClickListener {
            toggleFavorite(hotel, holder)
        }
    }

    private fun loadFavoriteStatus(hotel: Hotel, holder: ViewHolder) {
        fragment?.lifecycleScope?.launch {
            try {
                val currentUser = userPreferencesManager?.getCurrentUser()
                if (currentUser != null) {
                    val isFavorite = favoritesManager?.isFavorite(currentUser.email, hotel.id) ?: false
                    hotel.isFavorite = isFavorite

                    // Update UI on main thread
                    updateFavoriteIcon(hotel, holder)
                    updateContentDescriptions(hotel, holder)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun toggleFavorite(hotel: Hotel, holder: ViewHolder) {
        fragment?.lifecycleScope?.launch {
            try {
                val currentUser = userPreferencesManager?.getCurrentUser()
                if (currentUser != null) {
                    val success = favoritesManager?.toggleFavorite(currentUser.email, hotel.id) ?: false

                    if (success) {
                        // Update local state
                        hotel.isFavorite = !hotel.isFavorite

                        // Update UI
                        updateFavoriteIcon(hotel, holder)
                        updateContentDescriptions(hotel, holder)

                        // Announce change for accessibility
                        val announcement = if (hotel.isFavorite) {
                            "${hotel.name} added to favorites"
                        } else {
                            "${hotel.name} removed from favorites"
                        }
                        holder.ivFavorite.announceForAccessibility(announcement)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateFavoriteIcon(hotel: Hotel, holder: ViewHolder) {
        holder.ivFavorite.setImageResource(
            if (hotel.isFavorite) R.drawable.ic_favorite_filled
            else R.drawable.ic_favorite_outline
        )
    }

    private fun updateContentDescriptions(hotel: Hotel, holder: ViewHolder) {
        val favoriteStatus = if (hotel.isFavorite) "favorited" else "not favorited"
        holder.itemView.contentDescription = "${hotel.name}, ${hotel.location}, ${hotel.price} per night, ${hotel.rating} star rating, $favoriteStatus"

        holder.ivFavorite.contentDescription = if (hotel.isFavorite) {
            "Remove ${hotel.name} from favorites"
        } else {
            "Add ${hotel.name} to favorites"
        }
    }

    private fun onHotelClick(hotel: Hotel, holder: ViewHolder) {
        val context = holder.itemView.context
        val intent = HotelDetailActivity.createIntent(context, hotel.id)
        context.startActivity(intent)
    }


    override fun getItemCount(): Int = hotels.size
}