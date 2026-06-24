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

/**
 * ViewModel экрана броней пользователя: загружает список заказов с сервера,
 * кэширует их для офлайн-показа и удаляет брони. При недоступности сервера
 * показывает последние сохранённые данные и выставляет флаг [isOffline].
 */
class ScheduleScreenViewModel : ViewModel() {

    private val orderApi = RetrofitHelper.orderApi

    // --- Состояние UI ---

    var orders by mutableStateOf<List<OrderResponse>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // true, когда сервер недоступен, но мы показываем закэшированные брони.
    var isOffline by mutableStateOf(false)
        private set

    /**
     * Загружает брони пользователя и кэширует их. При ошибке откатывается на
     * локальный кэш (помечая [isOffline]); если кэша нет — показывает ошибку.
     */
    fun loadMyOrders() {
        // Загрузку выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор загрузки и сбрасываем прошлую ошибку.
            isLoading = true
            errorMessage = null
            try {
                // Запрашиваем брони пользователя с сервера.
                val fetched = orderApi.getMyOrders()
                orders = fetched
                // Данные свежие — выключаем офлайн-режим.
                isOffline = false
                // Сохраняем локальную копию, чтобы брони пережили переход в офлайн.
                OrdersCache.save(fetched)
            } catch (e: Exception) {
                Log.e("HotelApp", "Error loading my orders", e)
                // Офлайн (или ошибка сервера): пробуем последние закэшированные брони.
                val cached = OrdersCache.load()
                if (cached.isNotEmpty()) {
                    // Кэш есть — показываем его и помечаем офлайн-режим.
                    orders = cached
                    isOffline = true
                    errorMessage = null
                } else {
                    // Кэша нет — показываем ошибку загрузки.
                    isOffline = false
                    errorMessage = AppRes.str(R.string.error_load_bookings)
                }
            } finally {
                // В любом исходе снимаем индикатор загрузки.
                isLoading = false
            }
        }
    }

    /** Удаляет бронь и перезагружает список. */
    fun deleteOrder(id: Long) {
        // Удаление выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Сбрасываем прошлую ошибку.
            errorMessage = null
            try {
                // Удаляем бронь на сервере.
                orderApi.deleteOrder(id)
                // Перезагружаем список, чтобы убрать удалённую бронь.
                loadMyOrders()
            } catch (e: Exception) {
                // Ошибка удаления — показываем сообщение.
                Log.e("HotelApp", "Error deleting order $id", e)
                errorMessage = AppRes.str(R.string.error_delete_booking)
            }
        }
    }
}
