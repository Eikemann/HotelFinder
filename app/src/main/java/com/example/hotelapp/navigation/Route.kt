package com.example.hotelapp.navigation

sealed class Route {

    object Auth : Route() {
        const val route = "auth"
    }

    object Login : Route() {
        const val route = "login"
    }

    object Register : Route() {
        const val route = "register"
    }

    object Dashboard : Route() {
        const val route = "dashboard"
    }

    object Home : Route() {
        const val route = "home"
    }

    object Search : Route() {
        // Optional `city` query arg: empty means "show all hotels".
        const val base = "search"
        const val ARG_CITY = "city"
        const val route = "search?city={city}"
        fun createWithCity(city: String) = "search?city=$city"
    }

    object Schedule : Route() {
        const val route = "schedule"
    }

    object Profile : Route() {
        const val route = "profile"
    }

    object DetailGraph : Route() {
        const val route = "detail"
    }

    object HotelDetail : Route() {
        const val route = "hotel_detail/{hotelId}"
        fun create(hotelId: String) = "hotel_detail/$hotelId"
    }

    object RoomSelection : Route() {
        const val route = "room_selection"
    }

    object Booking : Route() {
        const val route = "booking"
    }
}
