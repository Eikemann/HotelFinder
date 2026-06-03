package com.example.hotelapp.data.remote.api

import com.example.hotelapp.domain.remote.model.booking.RoomResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface RoomApi {

    @GET("rooms/property/{propertyId}")
    suspend fun getRoomsForProperty(@Path("propertyId") propertyId: Long): List<RoomResponse>
}
