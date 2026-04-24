package com.example.hotelapp.data.remote.api

import com.example.hotelapp.domain.remote.model.HotelResponse
import com.example.hotelapp.domain.remote.model.HotelResponseItem
import retrofit2.http.GET
import retrofit2.http.Path

interface HotelApi {

    @GET("properties")
    suspend fun getHotels() : HotelResponse

    @GET("properties/{id}")
    suspend fun getHotelsById(@Path("id") id: Int): HotelResponseItem
}