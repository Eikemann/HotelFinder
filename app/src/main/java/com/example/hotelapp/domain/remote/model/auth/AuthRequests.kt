package com.example.hotelapp.domain.remote.model.auth

/** Mirrors backend `LoginRequest` / `RegisterRequest` / `AuthResponse` (simple-service). */

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String?,
    val userId: Long?,
    val email: String?,
    val fullName: String?,
    val role: String?
)
