package com.example.hotelapp.navigation

/**
 * Типобезопасный перечень маршрутов навигации и помощников для построения путей
 * с аргументами. Каждый экран/граф описан отдельным объектом-наследником.
 */
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
        // Необязательный query-аргумент `city`: пусто означает «показать все отели».
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

    object BookingDetail : Route() {
        const val ARG_ORDER_ID = "orderId"
        const val route = "booking_detail/{orderId}"
        fun create(orderId: Long) = "booking_detail/$orderId"
    }
}
