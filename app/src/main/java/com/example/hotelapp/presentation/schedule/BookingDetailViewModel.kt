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

/**
 * ViewModel экрана деталей одной брони: загружает заказ по id,
 * при недоступности сервера откатывается на локальный кэш броней.
 */
class BookingDetailViewModel : ViewModel() {

    private val orderApi = RetrofitHelper.orderApi

    var order by mutableStateOf<OrderResponse?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    /** Загружает бронь по id; при ошибке ищет её в локальном кэше. */
    fun load(orderId: Long) {
        // Загрузку выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор загрузки и сбрасываем прошлую ошибку.
            isLoading = true
            errorMessage = null
            try {
                // Запрашиваем бронь по id с сервера.
                order = orderApi.getOrder(orderId)
            } catch (e: Exception) {
                Log.e("HotelApp", "Error loading order $orderId", e)
                // Офлайн / ошибка сервера: ищем эту бронь в локальном кэше.
                val cached = OrdersCache.load().firstOrNull { it.id == orderId }
                if (cached != null) {
                    // Нашли в кэше — показываем её.
                    order = cached
                } else {
                    // В кэше нет — показываем ошибку загрузки.
                    errorMessage = AppRes.str(R.string.error_load_bookings)
                }
            } finally {
                // В любом исходе снимаем индикатор загрузки.
                isLoading = false
            }
        }
    }
}
