package com.example.hotelbookingapp1


object HotelDataManager {

    fun getNearLocationHotels(): List<Hotel> {
        return listOf(
            Hotel(
                id = 1,
                name = "The Aston Vill Hotel",
                location = "Alice Springs NT 0870, Australia",
                price = 200.7,
                rating = 5.0,
                imageRes = R.drawable.hotel_1,
                isFavorite = true,
                type = "Hotel"
            ),
            Hotel(
                id = 2,
                name = "Golden Paradise Resort",
                location = "Northern Territory, Australia",
                price = 175.9,
                rating = 4.8,
                imageRes = R.drawable.hotel_2,
                isFavorite = false,
                type = "Hotel"
            ),
            Hotel(
                id = 3,
                name = "Azure Beach Hotel",
                location = "Darwin NT 0800, Australia",
                price = 245.0,
                rating = 4.9,
                imageRes = R.drawable.hotel_3,
                isFavorite = false,
                type = "Hotel"
            ),
            Hotel(
                id = 4,
                name = "Outback Luxury Lodge",
                location = "Uluru NT 0872, Australia",
                price = 320.5,
                rating = 5.0,
                imageRes = R.drawable.hotel_4,
                isFavorite = true,
                type = "Hotel"
            ),
            Hotel(
                id = 5,
                name = "Desert Oasis Hotel",
                location = "Tennant Creek NT 0860, Australia",
                price = 185.0,
                rating = 4.7,
                imageRes = R.drawable.hotel_5,
                isFavorite = false,
                type = "Hotel"
            )
        )
    }

    fun getPopularHotels(): List<Hotel> {
        return listOf(
            Hotel(
                id = 6,
                name = "Asteria Hotel",
                location = "Wilora NT 0872, Australia",
                price = 165.3,
                rating = 5.0,
                imageRes = R.drawable.hotel_6,
                isFavorite = false,
                type = "Hotel"
            ),
            Hotel(
                id = 7,
                name = "Crown Plaza Hotel",
                location = "Katherine NT 0850, Australia",
                price = 210.0,
                rating = 4.6,
                imageRes = R.drawable.hotel_7,
                isFavorite = true,
                type = "Hotel"
            ),
            Hotel(
                id = 8,
                name = "Sunset Villa Resort",
                location = "Jabiru NT 0886, Australia",
                price = 195.5,
                rating = 4.8,
                imageRes = R.drawable.hotel_8,
                isFavorite = false,
                type = "Hotel"
            ),
            Hotel(
                id = 9,
                name = "Royal Garden Hotel",
                location = "Palmerston NT 0830, Australia",
                price = 155.0,
                rating = 4.5,
                imageRes = R.drawable.hotel_9,
                isFavorite = false,
                type = "Hotel"
            ),
            Hotel(
                id = 10,
                name = "Tropical Paradise Inn",
                location = "Nhulunbuy NT 0880, Australia",
                price = 225.0,
                rating = 4.9,
                imageRes = R.drawable.hotel_10,
                isFavorite = true,
                type = "Hotel"
            )
        )
    }

    // Homestay data for Near Location
    fun getNearLocationHomestays(): List<Hotel> {
        return listOf(
            Hotel(
                id = 11,
                name = "Cozy Family Homestay",
                location = "Alice Springs NT 0870, Australia",
                price = 85.0,
                rating = 4.6,
                imageRes = R.drawable.hotel_1, // You can use different images or same ones
                isFavorite = false,
                type = "Homestay"
            ),
            Hotel(
                id = 12,
                name = "Outback Heritage Home",
                location = "Darwin NT 0800, Australia",
                price = 95.5,
                rating = 4.7,
                imageRes = R.drawable.hotel_2,
                isFavorite = false,
                type = "Homestay"
            ),
            Hotel(
                id = 13,
                name = "Traditional Australian Home",
                location = "Katherine NT 0850, Australia",
                price = 75.0,
                rating = 4.5,
                imageRes = R.drawable.hotel_3,
                isFavorite = true,
                type = "Homestay"
            ),
            Hotel(
                id = 14,
                name = "Desert View Homestay",
                location = "Uluru NT 0872, Australia",
                price = 110.0,
                rating = 4.8,
                imageRes = R.drawable.hotel_4,
                isFavorite = false,
                type = "Homestay"
            )
        )
    }

    // Popular Homestays
    fun getPopularHomestays(): List<Hotel> {
        return listOf(
            Hotel(
                id = 15,
                name = "Authentic Aussie Experience",
                location = "Tennant Creek NT 0860, Australia",
                price = 120.0,
                rating = 4.9,
                imageRes = R.drawable.hotel_5,
                isFavorite = false,
                type = "Homestay"
            ),
            Hotel(
                id = 16,
                name = "Bush Telegraph Homestay",
                location = "Jabiru NT 0886, Australia",
                price = 90.0,
                rating = 4.6,
                imageRes = R.drawable.hotel_6,
                isFavorite = true,
                type = "Homestay"
            ),
            Hotel(
                id = 17,
                name = "Wanderer's Rest Home",
                location = "Palmerston NT 0830, Australia",
                price = 105.5,
                rating = 4.7,
                imageRes = R.drawable.hotel_7,
                isFavorite = false,
                type = "Homestay"
            )
        )
    }

    // Apartments for Near Location
    fun getNearLocationApartments(): List<Hotel> {
        return listOf(
            Hotel(
                id = 18,
                name = "Modern City Apartment",
                location = "Darwin NT 0800, Australia",
                price = 135.0,
                rating = 4.4,
                imageRes = R.drawable.hotel_8,
                isFavorite = false,
                type = "Apartment"
            ),
            Hotel(
                id = 19,
                name = "Executive Studio Suite",
                location = "Alice Springs NT 0870, Australia",
                price = 145.5,
                rating = 4.5,
                imageRes = R.drawable.hotel_9,
                isFavorite = true,
                type = "Apartment"
            ),
            Hotel(
                id = 20,
                name = "Riverside Apartment",
                location = "Katherine NT 0850, Australia",
                price = 125.0,
                rating = 4.3,
                imageRes = R.drawable.hotel_10,
                isFavorite = false,
                type = "Apartment"
            )
        )
    }

    // Popular Apartments
    fun getPopularApartments(): List<Hotel> {
        return listOf(
            Hotel(
                id = 21,
                name = "Luxury Penthouse Suite",
                location = "Darwin NT 0800, Australia",
                price = 280.0,
                rating = 4.8,
                imageRes = R.drawable.hotel_1,
                isFavorite = false,
                type = "Apartment"
            ),
            Hotel(
                id = 22,
                name = "Beachfront Apartment",
                location = "Nhulunbuy NT 0880, Australia",
                price = 195.0,
                rating = 4.6,
                imageRes = R.drawable.hotel_2,
                isFavorite = true,
                type = "Apartment"
            ),
            Hotel(
                id = 23,
                name = "City Centre Loft",
                location = "Palmerston NT 0830, Australia",
                price = 165.5,
                rating = 4.4,
                imageRes = R.drawable.hotel_3,
                isFavorite = false,
                type = "Apartment"
            ),
            Hotel(
                id = 24,
                name = "Designer Studio",
                location = "Jabiru NT 0886, Australia",
                price = 175.0,
                rating = 4.7,
                imageRes = R.drawable.hotel_4,
                isFavorite = false,
                type = "Apartment"
            )
        )
    }

    fun getAllHotels(): List<Hotel> {
        return getNearLocationHotels() + getPopularHotels() +
                getNearLocationHomestays() + getPopularHomestays() +
                getNearLocationApartments() + getPopularApartments()
    }

    fun getHotelById(hotelId: Int): Hotel? {
        return getAllHotels().find { it.id == hotelId }
    }
}