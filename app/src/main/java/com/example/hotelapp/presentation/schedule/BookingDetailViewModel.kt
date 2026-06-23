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
import com.example.hotelapp.data.remote.api.RetrofitHelper
import com.example.hotelapp.domain.remote.model.booking.OrderResponse
import kotlinx.coroutines.launch

class BookingDetailViewModel : ViewModel() {

    private val orderApi = RetrofitHelper.orderApi

    var order by mutableStateOf<OrderResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun load(orderId: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                order = orderApi.getOrder(orderId)
            } catch (e: Exception) {
                Log.e("HotelApp", "Error loading order $orderId", e)
                // Offline / server error: fall back to the locally cached booking.
                val cached = OrdersCache.load().firstOrNull { it.id == orderId }
                if (cached != null) {
                    order = cached
                } else {
                    errorMessage = AppRes.str(R.string.error_load_bookings)
                }
            } finally {
                isLoading = false
            }
        }
    }
}
