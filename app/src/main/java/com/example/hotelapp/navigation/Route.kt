package com.example.hotelapp.navigation

sealed class Route {

    // Root
    object Dashboard : Route() {
        const val route = "dashboard"
    }

    // Dashboard screens
    object Home : Route() {
        const val route = "home"
    }

    object Search : Route() {
        const val route = "search"
    }

    object Schedule : Route() {
        const val route = "schedule"
    }

    object Profile : Route() {
        const val route = "profile"
    }

    // Detail flow (GRAPH + DESTINATIONS)
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
