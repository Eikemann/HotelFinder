package com.example.hotelapp.presentation.booking

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
import com.example.hotelapp.data.remote.api.RoomApi
import com.example.hotelapp.data.remote.api.serverMessage
import com.example.hotelapp.domain.remote.model.booking.CreateOrderRequest
import com.example.hotelapp.domain.remote.model.booking.RoomResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.time.LocalDate

/**
 * ViewModel экрана бронирования: загружает комнаты объекта, хранит выбранную комнату,
 * считает итоговую стоимость и отправляет заказ через [OrderApi].
 * Состояние UI публикуется через Compose-состояния.
 */
class BookingViewModel : ViewModel() {

    private val roomApi = RetrofitHelper.roomApi
    private val orderApi = RetrofitHelper.orderApi

    // --- Состояние UI, наблюдаемое экраном бронирования ---

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

    /**
     * Загружает комнаты объекта и предвыбирает первую доступную
     * (или первую из списка, если доступных нет).
     */
    fun loadRooms(propertyId: Long) {
        // Сетевой вызов в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор загрузки комнат и сбрасываем прошлую ошибку.
            isLoadingRooms = true
            errorMessage = null
            try {
                // Запрашиваем комнаты выбранного объекта.
                val result = roomApi.getRoomsForProperty(propertyId)
                rooms = result
                // Предвыбираем первую доступную комнату, иначе — просто первую из списка.
                selectedRoom = result.firstOrNull { it.status == "AVAILABLE" } ?: result.firstOrNull()
            } catch (e: Exception) {
                // Ошибка загрузки — показываем сообщение «нет доступных комнат».
                Log.e("HotelApp", "Error loading rooms for $propertyId", e)
                errorMessage = AppRes.str(R.string.booking_no_rooms)
            } finally {
                // В любом исходе снимаем индикатор загрузки.
                isLoadingRooms = false
            }
        }
    }

    /** Запоминает выбранную пользователем комнату. */
    fun selectRoom(room: RoomResponse) {
        selectedRoom = room
    }

    /**
     * Подтверждает бронь: проверяет выбор комнаты и корректность дат, отправляет заказ
     * и обновляет локальный кэш броней. Сумму считает бэкенд, не клиент.
     */
    fun confirmBooking(propertyId: Long, checkIn: LocalDate, checkOut: LocalDate) {
        // Комната должна быть выбрана — иначе выходим с сообщением об ошибке.
        val room = selectedRoom
        if (room == null) {
            errorMessage = AppRes.str(R.string.error_select_room)
            return
        }
        // Дата выезда должна быть позже даты заезда.
        if (!checkOut.isAfter(checkIn)) {
            errorMessage = AppRes.str(R.string.error_checkout_after)
            return
        }
        // Отправку заказа выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор отправки и сбрасываем прошлую ошибку.
            isSubmitting = true
            errorMessage = null
            try {
                // Создаём заказ; даты передаём строкой ISO, сумму считает бэкенд.
                orderApi.createOrder(
                    CreateOrderRequest(
                        propertyId = propertyId,
                        roomId = room.id,
                        checkInDate = checkIn.toString(),
                        checkOutDate = checkOut.toString()
                    )
                )
                // Поднимаем флаг успеха — экран покажет подтверждение.
                bookingConfirmed = true

                // Обновляем локальный кэш броней, пока есть сеть (best-effort:
                // ошибка кэширования не должна срывать успешное бронирование).
                runCatching { OrdersCache.save(orderApi.getMyOrders()) }
                    .onFailure { Log.w("HotelApp", "Could not refresh orders cache after booking", it) }
            } catch (e: HttpException) {
                // Ответ-ошибка от сервера: показываем его сообщение либо общий текст.
                errorMessage = e.serverMessage()
                    ?: AppRes.str(R.string.error_booking_failed)
            } catch (e: Exception) {
                // До сервера не дошли — сообщение о проблеме сети.
                Log.e("HotelApp", "Error creating order", e)
                errorMessage = AppRes.str(R.string.error_network)
            } finally {
                // В любом исходе снимаем индикатор отправки.
                isSubmitting = false
            }
        }
    }

    /** Предварительно считает стоимость для UI (цена × число ночей); null при некорректных датах. */
    fun nightlyTotal(checkIn: LocalDate, checkOut: LocalDate): Double? {
        // Без выбранной комнаты цену посчитать нельзя.
        val price = selectedRoom?.pricePerNight ?: return null
        // Считаем число ночей между датами.
        val nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut)
        // Некорректный диапазон дат (0 или меньше ночей) — стоимости нет.
        if (nights <= 0) return null
        // Итог: цена за ночь × число ночей.
        return price * nights
    }
}
