package com.example.hotelbookingapp1

data class Hotel(
    val id: Int,
    val name: String,
    val location: String,
    val price: Double,
    val rating: Double,
    val imageRes: Int,
    var isFavorite: Boolean = false,
    val type: String = "Hotel"
)