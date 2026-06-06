package com.example.hotelapp.presentation.schedule

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.R
import com.example.hotelapp.data.AppRes
import com.example.hotelapp.data.remote.api.OrderApi
import com.example.hotelapp.data.remote.api.RetrofitHelper
import com.example.hotelapp.domain.remote.model.booking.OrderResponse
import kotlinx.coroutines.launch

class ScheduleScreenViewModel : ViewModel() {

    private val orderApi = RetrofitHelper.getInstance().create(OrderApi::class.java)

    var orders by mutableStateOf<List<OrderResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadMyOrders() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                orders = orderApi.getMyOrders()
            } catch (e: Exception) {
                Log.e("HotelApp", "Error loading my orders", e)
                errorMessage = AppRes.str(R.string.error_load_bookings)
            } finally {
                isLoading = false
            }
        }
    }
}
