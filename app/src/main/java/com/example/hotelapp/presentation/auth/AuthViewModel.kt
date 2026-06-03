package com.example.hotelapp.presentation.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.data.auth.TokenManager
import com.example.hotelapp.data.remote.api.AuthApi
import com.example.hotelapp.data.remote.api.RetrofitHelper
import com.example.hotelapp.domain.remote.model.auth.LoginRequest
import com.example.hotelapp.domain.remote.model.auth.RegisterRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel : ViewModel() {

    private val authApi = RetrofitHelper.getInstance().create(AuthApi::class.java)

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAuthenticated by mutableStateOf(false)
        private set

    fun login(email: String, password: String) {
        if (!validate(email = email, password = password)) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = authApi.login(LoginRequest(email.trim(), password))
                TokenManager.save(response)
                isAuthenticated = true
            } catch (e: HttpException) {
                errorMessage = if (e.code() == 401 || e.code() == 403) {
                    "Invalid email or password."
                } else {
                    "Login failed (${e.code()})."
                }
            } catch (e: Exception) {
                Log.e("HotelApp", "Login error", e)
                errorMessage = "Could not reach the server. Check your connection."
            } finally {
                isLoading = false
            }
        }
    }

    fun register(fullName: String, email: String, password: String) {
        if (fullName.isBlank()) {
            errorMessage = "Please enter your full name."
            return
        }
        if (!validate(email = email, password = password, minPassword = 6)) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = authApi.register(
                    RegisterRequest(fullName.trim(), email.trim(), password)
                )
                TokenManager.save(response)
                isAuthenticated = true
            } catch (e: HttpException) {
                errorMessage = if (e.code() == 409 || e.code() == 400) {
                    "That email may already be registered."
                } else {
                    "Registration failed (${e.code()})."
                }
            } catch (e: Exception) {
                Log.e("HotelApp", "Register error", e)
                errorMessage = "Could not reach the server. Check your connection."
            } finally {
                isLoading = false
            }
        }
    }

    fun consumeError() {
        errorMessage = null
    }

    private fun validate(email: String, password: String, minPassword: Int = 1): Boolean {
        if (email.isBlank() || !email.contains("@")) {
            errorMessage = "Please enter a valid email."
            return false
        }
        if (password.length < minPassword) {
            errorMessage = if (minPassword > 1) {
                "Password must be at least $minPassword characters."
            } else {
                "Please enter your password."
            }
            return false
        }
        return true
    }
}
