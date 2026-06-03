package com.example.hotelapp.presentation.booking

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.data.remote.api.OrderApi
import com.example.hotelapp.data.remote.api.RetrofitHelper
import com.example.hotelapp.data.remote.api.RoomApi
import com.example.hotelapp.data.remote.api.serverMessage
import com.example.hotelapp.domain.remote.model.booking.CreateOrderRequest
import com.example.hotelapp.domain.remote.model.booking.RoomResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate

class BookingViewModel : ViewModel() {

    private val roomApi = RetrofitHelper.getInstance().create(RoomApi::class.java)
    private val orderApi = RetrofitHelper.getInstance().create(OrderApi::class.java)

    var rooms by mutableStateOf<List<RoomResponse>>(emptyList())
        private set

    var selectedRoom by mutableStateOf<RoomResponse?>(null)
        private set

    var isLoadingRooms by mutableStateOf(false)
        private set

    var isSubmitting by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var bookingConfirmed by mutableStateOf(false)
        private set

    fun loadRooms(propertyId: Long) {
        viewModelScope.launch {
            isLoadingRooms = true
            errorMessage = null
            try {
                val result = roomApi.getRoomsForProperty(propertyId)
                rooms = result
                selectedRoom = result.firstOrNull { it.status == "AVAILABLE" } ?: result.firstOrNull()
            } catch (e: Exception) {
                Log.e("HotelApp", "Error loading rooms for $propertyId", e)
                errorMessage = "Could not load rooms for this property."
            } finally {
                isLoadingRooms = false
            }
        }
    }

    fun selectRoom(room: RoomResponse) {
        selectedRoom = room
    }

    fun confirmBooking(propertyId: Long, checkIn: LocalDate, checkOut: LocalDate) {
        val room = selectedRoom
        if (room == null) {
            errorMessage = "Please select a room."
            return
        }
        if (!checkOut.isAfter(checkIn)) {
            errorMessage = "Check-out must be after check-in."
            return
        }
        viewModelScope.launch {
            isSubmitting = true
            errorMessage = null
            try {
                orderApi.createOrder(
                    CreateOrderRequest(
                        propertyId = propertyId,
                        roomId = room.id,
                        checkInDate = checkIn.toString(),
                        checkOutDate = checkOut.toString()
                    )
                )
                bookingConfirmed = true
            } catch (e: HttpException) {
                errorMessage = e.serverMessage()
                    ?: "Booking failed. The room may be unavailable for those dates."
            } catch (e: Exception) {
                Log.e("HotelApp", "Error creating order", e)
                errorMessage = "Could not reach the server. Check your connection."
            } finally {
                isSubmitting = false
            }
        }
    }

    fun nightlyTotal(checkIn: LocalDate, checkOut: LocalDate): Double? {
        val price = selectedRoom?.pricePerNight ?: return null
        val nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut)
        if (nights <= 0) return null
        return price * nights
    }
}
