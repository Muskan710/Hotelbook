package com.example.hotelbookingapp1

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

data class Booking(
    val id: String,
    val hotelId: Int,
    val hotelName: String,
    val hotelLocation: String,
    val hotelImageRes: Int,
    val hotelPrice: Double,
    val hotelRating: Double,
    val checkInDate: String,
    val bookingTimestamp: Long = System.currentTimeMillis()
)

class BookingsManager(private val context: Context) {

    private val fileName = "bookings_data.json"

    private fun getFile(): File {
        return File(context.filesDir, fileName)
    }

    private fun bookingToJson(booking: Booking): JSONObject {
        return JSONObject().apply {
            put("id", booking.id)
            put("hotelId", booking.hotelId)
            put("hotelName", booking.hotelName)
            put("hotelLocation", booking.hotelLocation)
            put("hotelImageRes", booking.hotelImageRes)
            put("hotelPrice", booking.hotelPrice)
            put("hotelRating", booking.hotelRating)
            put("checkInDate", booking.checkInDate)
            put("bookingTimestamp", booking.bookingTimestamp)
        }
    }

    private fun jsonToBooking(jsonObject: JSONObject): Booking {
        return Booking(
            id = jsonObject.getString("id"),
            hotelId = jsonObject.getInt("hotelId"),
            hotelName = jsonObject.getString("hotelName"),
            hotelLocation = jsonObject.getString("hotelLocation"),
            hotelImageRes = jsonObject.getInt("hotelImageRes"),
            hotelPrice = jsonObject.getDouble("hotelPrice"),
            hotelRating = jsonObject.getDouble("hotelRating"),
            checkInDate = jsonObject.getString("checkInDate"),
            bookingTimestamp = jsonObject.optLong("bookingTimestamp", System.currentTimeMillis())
        )
    }

    private suspend fun readBookingsData(): Map<String, MutableList<Booking>> {
        return withContext(Dispatchers.IO) {
            try {
                val file = getFile()
                if (!file.exists()) {
                    return@withContext emptyMap()
                }

                val jsonString = file.readText()
                if (jsonString.isEmpty()) {
                    return@withContext emptyMap()
                }

                val jsonObject = JSONObject(jsonString)
                val userBookingsMap = mutableMapOf<String, MutableList<Booking>>()

                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val userEmail = keys.next()
                    val bookingsArray = jsonObject.getJSONArray(userEmail)
                    val bookingsList = mutableListOf<Booking>()

                    for (i in 0 until bookingsArray.length()) {
                        val bookingJson = bookingsArray.getJSONObject(i)
                        bookingsList.add(jsonToBooking(bookingJson))
                    }

                    userBookingsMap[userEmail] = bookingsList
                }

                userBookingsMap
            } catch (e: Exception) {
                Log.e("BookingsManager", "Error reading bookings data", e)
                emptyMap()
            }
        }
    }

    private suspend fun writeBookingsData(userBookingsMap: Map<String, List<Booking>>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = getFile()
                val jsonObject = JSONObject()

                userBookingsMap.forEach { (userEmail, bookings) ->
                    val bookingsArray = JSONArray()
                    bookings.forEach { booking ->
                        bookingsArray.put(bookingToJson(booking))
                    }
                    jsonObject.put(userEmail, bookingsArray)
                }

                file.writeText(jsonObject.toString(2))
                true
            } catch (e: IOException) {
                Log.e("BookingsManager", "IO Error writing bookings data", e)
                false
            } catch (e: JSONException) {
                Log.e("BookingsManager", "JSON Error writing bookings data", e)
                false
            }
        }
    }

    suspend fun addBooking(userEmail: String, hotel: Hotel, checkInDate: String): Boolean {
        return try {
            val userBookingsMap = readBookingsData().toMutableMap()
            val userBookings = userBookingsMap[userEmail]?.toMutableList() ?: mutableListOf()

            val bookingId = "${hotel.id}_${System.currentTimeMillis()}"
            val newBooking = Booking(
                id = bookingId,
                hotelId = hotel.id,
                hotelName = hotel.name,
                hotelLocation = hotel.location,
                hotelImageRes = hotel.imageRes,
                hotelPrice = hotel.price,
                hotelRating = hotel.rating,
                checkInDate = checkInDate
            )

            userBookings.add(newBooking)
            userBookingsMap[userEmail] = userBookings

            writeBookingsData(userBookingsMap)
        } catch (e: Exception) {
            Log.e("BookingsManager", "Error adding booking", e)
            false
        }
    }

    suspend fun getUserBookings(userEmail: String): List<Booking> {
        return try {
            val userBookingsMap = readBookingsData()
            userBookingsMap[userEmail]?.sortedByDescending { it.bookingTimestamp } ?: emptyList()
        } catch (e: Exception) {
            Log.e("BookingsManager", "Error getting user bookings", e)
            emptyList()
        }
    }

    suspend fun cancelBooking(userEmail: String, bookingId: String): Boolean {
        return try {
            val userBookingsMap = readBookingsData().toMutableMap()
            val userBookings = userBookingsMap[userEmail]?.toMutableList()

            if (userBookings != null) {
                val bookingIndex = userBookings.indexOfFirst { it.id == bookingId }
                if (bookingIndex != -1) {
                    userBookings.removeAt(bookingIndex)
                    userBookingsMap[userEmail] = userBookings
                    writeBookingsData(userBookingsMap)
                } else {
                    false
                }
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("BookingsManager", "Error canceling booking", e)
            false
        }
    }

    suspend fun hasBooking(userEmail: String, hotelId: Int): Boolean {
        return try {
            val userBookings = getUserBookings(userEmail)
            userBookings.any { it.hotelId == hotelId }
        } catch (e: Exception) {
            Log.e("BookingsManager", "Error checking booking exists", e)
            false
        }
    }
}