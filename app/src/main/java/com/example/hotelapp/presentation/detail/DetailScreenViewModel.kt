package com.example.hotelapp.presentation.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.R
import com.example.hotelapp.data.AppRes
import com.example.hotelapp.data.sampleHotels
import com.example.hotelapp.data.mapper.toHotel
import com.example.hotelapp.data.remote.api.HotelApi
import com.example.hotelapp.data.remote.api.RetrofitHelper
import com.example.hotelapp.data.remote.api.ReviewApi
import com.example.hotelapp.data.remote.api.serverMessage
import com.example.hotelapp.domain.local.model.Hotel
import com.example.hotelapp.domain.remote.model.review.CreateReviewRequest
import com.example.hotelapp.domain.remote.model.review.ReviewResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

/**
 * ViewModel экрана деталей отеля: загружает сам отель и его отзывы,
 * а также отправляет новый отзыв. При сбое сети подставляет офлайн-данные.
 */
class DetailScreenViewModel : ViewModel() {

    private val hotelApi = RetrofitHelper.hotelApi
    private val reviewApi = RetrofitHelper.reviewApi

    // --- Состояние UI ---

    var hotel by mutableStateOf<Hotel?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var reviews by mutableStateOf<List<ReviewResponse>>(emptyList())
        private set

    var isSubmittingReview by mutableStateOf(false)
        private set

    var reviewError by mutableStateOf<String?>(null)
        private set

    var reviewJustSubmitted by mutableStateOf(false)
        private set

    /** Загружает отель по id, затем подгружает его отзывы; при ошибке — офлайн-данные. */
    fun loadHotelById(hotelId: Int) {
        // Загрузку выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор загрузки и сбрасываем прошлую ошибку.
            isLoading = true
            errorMessage = null
            try {
                // Запрашиваем отель по id в фоновом потоке (IO).
                val response = withContext(Dispatchers.IO) {
                    hotelApi.getHotelsById(hotelId)
                }
                // Преобразуем сетевую модель в доменную для UI.
                hotel = response.toHotel()
                Log.d("HotelApp", "Hotel $hotelId fetched from API")
            } catch (e: Exception) {
                // Ошибка сети: показываем уведомление и подставляем офлайн-данные.
                Log.e("HotelApp", "Error fetching hotel $hotelId", e)
                errorMessage = AppRes.str(R.string.error_offline_data)
                hotel = sampleHotels.find { it.id == hotelId }
            } finally {
                // В любом исходе снимаем индикатор загрузки.
                isLoading = false
            }
            // После отеля подгружаем его отзывы.
            loadReviews(hotelId)
        }
    }

    /** Загружает отзывы об объекте; ошибки игнорируются (список просто остаётся пустым). */
    fun loadReviews(propertyId: Int) {
        // Загрузку выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            try {
                // Запрашиваем отзывы об объекте и кладём в состояние.
                reviews = reviewApi.getReviewsForProperty(propertyId.toLong())
            } catch (e: Exception) {
                Log.e("HotelApp", "Error loading reviews for $propertyId", e)
                // Отзывы некритичны; при ошибке оставляем список пустым.
            }
        }
    }

    /**
     * Отправляет отзыв: проверяет заголовок, ограничивает оценку диапазоном 1..10,
     * затем перезагружает список отзывов. Ошибки публикуются в [reviewError].
     */
    fun submitReview(propertyId: Int, rating: Int, title: String, comment: String) {
        // Заголовок обязателен — без него выходим с сообщением об ошибке.
        if (title.isBlank()) {
            reviewError = AppRes.str(R.string.error_review_title)
            return
        }
        // Отправку выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор отправки и сбрасываем прошлую ошибку.
            isSubmittingReview = true
            reviewError = null
            try {
                // Создаём отзыв: оценку ограничиваем диапазоном 1..10, поля обрезаем,
                // пустой комментарий заменяем на null.
                reviewApi.createReview(
                    CreateReviewRequest(
                        propertyId = propertyId.toLong(),
                        rating = rating.coerceIn(1, 10),
                        title = title.trim(),
                        comment = comment.trim().ifBlank { null }
                    )
                )
                // Поднимаем флаг успеха (экран закроет форму).
                reviewJustSubmitted = true
                // Перечитываем список, чтобы новый отзыв сразу отобразился.
                loadReviews(propertyId)
            } catch (e: HttpException) {
                // Ответ-ошибка от сервера: показываем его сообщение либо общий текст.
                reviewError = e.serverMessage() ?: AppRes.str(R.string.error_review_submit)
            } catch (e: Exception) {
                // До сервера не дошли — сообщение о проблеме сети.
                Log.e("HotelApp", "Error submitting review", e)
                reviewError = AppRes.str(R.string.error_review_network)
            } finally {
                // В любом исходе снимаем индикатор отправки.
                isSubmittingReview = false
            }
        }
    }

    /** Сбрасывает ошибку отзыва после показа в UI. */
    fun consumeReviewError() {
        // Очищаем текст ошибки, чтобы он не показался повторно.
        reviewError = null
    }

    /** Сбрасывает флаг успешной отправки отзыва после реакции UI. */
    fun consumeReviewSubmitted() {
        // Опускаем флаг, чтобы реакция (закрытие формы) не повторялась.
        reviewJustSubmitted = false
    }
}
