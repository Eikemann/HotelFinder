package com.example.hotelapp.presentation.schedule

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.R
import com.example.hotelapp.data.AppRes
import com.example.hotelapp.data.local.OrdersCache
import com.example.hotelapp.data.remote.api.OrderApi
import com.example.hotelapp.data.remote.api.RetrofitHelper
import com.example.hotelapp.domain.remote.model.booking.OrderResponse
import kotlinx.coroutines.launch

class ScheduleScreenViewModel : ViewModel() {

    private val orderApi = RetrofitHelper.orderApi

    var orders by mutableStateOf<List<OrderResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // True when the server was unreachable but we're showing cached bookings.
    var isOffline by mutableStateOf(false)
        private set

    fun loadMyOrders() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val fetched = orderApi.getMyOrders()
                orders = fetched
                isOffline = false
                // Keep a local copy so bookings survive going offline.
                OrdersCache.save(fetched)
            } catch (e: Exception) {
                Log.e("HotelApp", "Error loading my orders", e)
                // Offline (or server error): fall back to the last cached bookings.
                val cached = OrdersCache.load()
                if (cached.isNotEmpty()) {
                    orders = cached
                    isOffline = true
                    errorMessage = null
                } else {
                    isOffline = false
                    errorMessage = AppRes.str(R.string.error_load_bookings)
                }
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteOrder(id: Long) {
        viewModelScope.launch {
            errorMessage = null
            try {
                orderApi.deleteOrder(id)
                loadMyOrders()
            } catch (e: Exception) {
                Log.e("HotelApp", "Error deleting order $id", e)
                errorMessage = AppRes.str(R.string.error_delete_booking)
            }
        }
    }
}
