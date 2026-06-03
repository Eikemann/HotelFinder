package com.example.hotelapp.data.remote.api

import com.example.hotelapp.domain.remote.model.auth.AuthResponse
import com.example.hotelapp.domain.remote.model.auth.LoginRequest
import com.example.hotelapp.domain.remote.model.auth.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse
}
