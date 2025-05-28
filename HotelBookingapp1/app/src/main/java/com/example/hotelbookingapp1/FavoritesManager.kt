package com.example.hotelbookingapp1

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class FavoritesManager(private val context: Context) {

    private val fileName = "user_favorites.json"

    private fun getFile(): java.io.File {
        return java.io.File(context.filesDir, fileName)
    }

    // Read favorites data from JSON file
    private suspend fun readFavoritesData(): MutableMap<String, MutableSet<Int>> {
        return withContext(Dispatchers.IO) {
            try {
                val file = getFile()
                if (!file.exists()) {
                    return@withContext mutableMapOf()
                }

                val jsonString = file.readText()
                if (jsonString.isEmpty()) {
                    return@withContext mutableMapOf()
                }

                val jsonObject = JSONObject(jsonString)
                val favoritesMap = mutableMapOf<String, MutableSet<Int>>()

                val keys = jsonObject.keys()
                while (keys.hasNext()) {
                    val userEmail = keys.next()
                    val favoritesArray = jsonObject.getJSONArray(userEmail)
                    val favoriteIds = mutableSetOf<Int>()

                    for (i in 0 until favoritesArray.length()) {
                        favoriteIds.add(favoritesArray.getInt(i))
                    }

                    favoritesMap[userEmail] = favoriteIds
                }

                favoritesMap
            } catch (e: Exception) {
                e.printStackTrace()
                mutableMapOf()
            }
        }
    }

    // Write favorites data to JSON file
    private suspend fun writeFavoritesData(favoritesMap: Map<String, Set<Int>>): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = getFile()
                val jsonObject = JSONObject()

                favoritesMap.forEach { (userEmail, favoriteIds) ->
                    val favoritesArray = JSONArray()
                    favoriteIds.forEach { id ->
                        favoritesArray.put(id)
                    }
                    jsonObject.put(userEmail, favoritesArray)
                }

                file.writeText(jsonObject.toString(2))
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    // Add hotel to favorites for specific user
    suspend fun addToFavorites(userEmail: String, hotelId: Int): Boolean {
        return try {
            val favoritesMap = readFavoritesData()
            val userFavorites = favoritesMap.getOrDefault(userEmail, mutableSetOf())
            userFavorites.add(hotelId)
            favoritesMap[userEmail] = userFavorites

            writeFavoritesData(favoritesMap)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Remove hotel from favorites for specific user
    suspend fun removeFromFavorites(userEmail: String, hotelId: Int): Boolean {
        return try {
            val favoritesMap = readFavoritesData()
            val userFavorites = favoritesMap.getOrDefault(userEmail, mutableSetOf())
            userFavorites.remove(hotelId)

            if (userFavorites.isEmpty()) {
                favoritesMap.remove(userEmail)
            } else {
                favoritesMap[userEmail] = userFavorites
            }

            writeFavoritesData(favoritesMap)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Check if hotel is in favorites for specific user
    suspend fun isFavorite(userEmail: String, hotelId: Int): Boolean {
        return try {
            val favoritesMap = readFavoritesData()
            val userFavorites = favoritesMap.getOrDefault(userEmail, mutableSetOf())
            userFavorites.contains(hotelId)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Get all favorite hotel IDs for specific user
    suspend fun getUserFavorites(userEmail: String): Set<Int> {
        return try {
            val favoritesMap = readFavoritesData()
            favoritesMap.getOrDefault(userEmail, mutableSetOf())
        } catch (e: Exception) {
            e.printStackTrace()
            emptySet()
        }
    }

    // Get all favorite hotels for specific user
    suspend fun getUserFavoriteHotels(userEmail: String): List<Hotel> {
        return try {
            val favoriteIds = getUserFavorites(userEmail)
            val allHotels = HotelDataManager.getAllHotels()
            allHotels.filter { hotel -> favoriteIds.contains(hotel.id) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Toggle favorite status
    suspend fun toggleFavorite(userEmail: String, hotelId: Int): Boolean {
        return if (isFavorite(userEmail, hotelId)) {
            removeFromFavorites(userEmail, hotelId)
        } else {
            addToFavorites(userEmail, hotelId)
        }
    }

    // Clear all favorites for a user
    suspend fun clearUserFavorites(userEmail: String): Boolean {
        return try {
            val favoritesMap = readFavoritesData()
            favoritesMap.remove(userEmail)
            writeFavoritesData(favoritesMap)
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}