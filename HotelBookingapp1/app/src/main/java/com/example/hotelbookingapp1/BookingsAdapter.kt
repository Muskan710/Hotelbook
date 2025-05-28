package com.example.hotelbookingapp1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BookingsAdapter(
    private val onCancelClick: (Booking) -> Unit
) : RecyclerView.Adapter<BookingsAdapter.BookingViewHolder>() {

    private var bookings = listOf<Booking>()

    fun updateBookings(newBookings: List<Booking>) {
        bookings = newBookings
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_booking, parent, false)
        return BookingViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int = bookings.size

    inner class BookingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivHotelImage: ImageView = itemView.findViewById(R.id.ivHotelImage)
        private val tvHotelName: TextView = itemView.findViewById(R.id.tvHotelName)
        private val tvHotelLocation: TextView = itemView.findViewById(R.id.tvHotelLocation)
        private val tvBookingDate: TextView = itemView.findViewById(R.id.tvBookingDate)
        private val tvHotelPrice: TextView = itemView.findViewById(R.id.tvHotelPrice)
        private val tvHotelRating: TextView = itemView.findViewById(R.id.tvHotelRating)
        private val ivCancelBooking: ImageView = itemView.findViewById(R.id.ivCancelBooking)

        fun bind(booking: Booking) {
            ivHotelImage.setImageResource(booking.hotelImageRes)
            tvHotelName.text = booking.hotelName
            tvHotelLocation.text = booking.hotelLocation
            tvBookingDate.text = "Check-in: ${booking.checkInDate}"
            tvHotelPrice.text = "$${booking.hotelPrice}/night"
            tvHotelRating.text = booking.hotelRating.toString()

            // Set content descriptions for accessibility
            ivHotelImage.contentDescription = "Image of ${booking.hotelName}"
            tvHotelRating.contentDescription = "${booking.hotelRating} star rating"
            ivCancelBooking.contentDescription = "Cancel booking for ${booking.hotelName}"

            // Cancel booking click listener
            ivCancelBooking.setOnClickListener {
                onCancelClick(booking)
            }

            // Optional: Click on item to view hotel details
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = HotelDetailActivity.createIntent(context, booking.hotelId)
                context.startActivity(intent)
            }
        }
    }
}