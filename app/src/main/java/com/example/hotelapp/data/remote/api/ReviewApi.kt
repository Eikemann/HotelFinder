package com.example.hotelapp.data.remote.api

import com.example.hotelapp.domain.remote.model.review.CreateReviewRequest
import com.example.hotelapp.domain.remote.model.review.ReviewResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReviewApi {

    @GET("reviews/property/{propertyId}")
    suspend fun getReviewsForProperty(@Path("propertyId") propertyId: Long): List<ReviewResponse>

    @POST("reviews")
    suspend fun createReview(@Body request: CreateReviewRequest)
}
