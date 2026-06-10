package com.example.hotelapp.presentation.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.R
import com.example.hotelapp.data.AppRes
import com.example.hotelapp.data.auth.TokenManager
import com.example.hotelapp.data.remote.api.AuthApi
import com.example.hotelapp.data.remote.api.RetrofitHelper
import com.example.hotelapp.domain.remote.model.auth.LoginRequest
import com.example.hotelapp.domain.remote.model.auth.RegisterRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AuthViewModel : ViewModel() {

    private val authApi = RetrofitHelper.authApi

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
                    AppRes.str(R.string.error_login_invalid)
                } else {
                    AppRes.str(R.string.error_login_failed, e.code())
                }
            } catch (e: Exception) {
                Log.e("HotelApp", "Login error", e)
                errorMessage = AppRes.str(R.string.error_network)
            } finally {
                isLoading = false
            }
        }
    }

    fun register(fullName: String, email: String, password: String) {
        if (fullName.isBlank()) {
            errorMessage = AppRes.str(R.string.error_enter_full_name)
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
                    AppRes.str(R.string.error_email_taken)
                } else {
                    AppRes.str(R.string.error_register_failed, e.code())
                }
            } catch (e: Exception) {
                Log.e("HotelApp", "Register error", e)
                errorMessage = AppRes.str(R.string.error_network)
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
            errorMessage = AppRes.str(R.string.error_invalid_email)
            return false
        }
        if (password.length < minPassword) {
            errorMessage = if (minPassword > 1) {
                AppRes.str(R.string.error_password_min, minPassword)
            } else {
                AppRes.str(R.string.error_enter_password)
            }
            return false
        }
        return true
    }
}
