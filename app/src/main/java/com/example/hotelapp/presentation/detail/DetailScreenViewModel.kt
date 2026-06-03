package com.example.hotelapp.presentation.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

class DetailScreenViewModel : ViewModel() {

    private val hotelApi = RetrofitHelper.getInstance().create(HotelApi::class.java)
    private val reviewApi = RetrofitHelper.getInstance().create(ReviewApi::class.java)

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

    fun loadHotelById(hotelId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO) {
                    hotelApi.getHotelsById(hotelId)
                }
                hotel = response.toHotel()
                Log.d("HotelApp", "Hotel $hotelId fetched from API")
            } catch (e: Exception) {
                Log.e("HotelApp", "Error fetching hotel $hotelId", e)
                errorMessage = "Could not connect to server. Showing offline data."
                hotel = sampleHotels.find { it.id == hotelId }
            } finally {
                isLoading = false
            }
            loadReviews(hotelId)
        }
    }

    fun loadReviews(propertyId: Int) {
        viewModelScope.launch {
            try {
                reviews = reviewApi.getReviewsForProperty(propertyId.toLong())
            } catch (e: Exception) {
                Log.e("HotelApp", "Error loading reviews for $propertyId", e)
                // Reviews are non-critical; leave the list empty on failure.
            }
        }
    }

    fun submitReview(propertyId: Int, rating: Int, title: String, comment: String) {
        if (title.isBlank()) {
            reviewError = "Please add a title."
            return
        }
        viewModelScope.launch {
            isSubmittingReview = true
            reviewError = null
            try {
                reviewApi.createReview(
                    CreateReviewRequest(
                        propertyId = propertyId.toLong(),
                        rating = rating.coerceIn(1, 10),
                        title = title.trim(),
                        comment = comment.trim().ifBlank { null }
                    )
                )
                reviewJustSubmitted = true
                loadReviews(propertyId)
            } catch (e: HttpException) {
                reviewError = e.serverMessage() ?: "Could not submit your review."
            } catch (e: Exception) {
                Log.e("HotelApp", "Error submitting review", e)
                reviewError = "Could not reach the server."
            } finally {
                isSubmittingReview = false
            }
        }
    }

    fun consumeReviewError() {
        reviewError = null
    }

    fun consumeReviewSubmitted() {
        reviewJustSubmitted = false
    }
}
