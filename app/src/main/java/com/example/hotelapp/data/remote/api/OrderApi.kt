package com.example.hotelapp.data.remote.api

import com.example.hotelapp.domain.remote.model.booking.CreateOrderRequest
import com.example.hotelapp.domain.remote.model.booking.OrderResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface OrderApi {

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest)

    @GET("orders/my")
    suspend fun getMyOrders(): List<OrderResponse>
}
