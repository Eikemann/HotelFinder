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

/**
 * ViewModel экранов входа и регистрации. Валидирует ввод, обращается к [AuthApi],
 * сохраняет токен в [TokenManager] и публикует состояние UI (загрузка, ошибка, успех)
 * через Compose-состояния.
 */
class AuthViewModel : ViewModel() {

    private val authApi = RetrofitHelper.authApi

    // --- Состояние UI, наблюдаемое экранами ---

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAuthenticated by mutableStateOf(false)
        private set

    /**
     * Выполняет вход: валидирует поля, вызывает API и сохраняет токен.
     * Ошибки 401/403 трактуются как неверные учётные данные, прочие — как сбой сети/сервера.
     */
    fun login(email: String, password: String) {
        // Проверяем ввод; при ошибке текст уже записан в errorMessage — выходим без запроса.
        if (!validate(email = email, password = password)) return
        // Сетевой вызов выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор загрузки и сбрасываем прошлую ошибку.
            isLoading = true
            errorMessage = null
            try {
                // Отправляем запрос входа (email обрезаем от пробелов).
                val response = authApi.login(LoginRequest(email.trim(), password))
                // Сохраняем токен и данные пользователя.
                TokenManager.save(response)
                // Поднимаем флаг успеха — экран по нему перейдёт дальше.
                isAuthenticated = true
            } catch (e: HttpException) {
                // Ответ-ошибка от сервера: 401/403 — неверные данные, иначе общий сбой входа.
                errorMessage = if (e.code() == 401 || e.code() == 403) {
                    AppRes.str(R.string.error_login_invalid)
                } else {
                    AppRes.str(R.string.error_login_failed, e.code())
                }
            } catch (e: Exception) {
                // До сервера не дошли (нет сети и т.п.) — сообщение о проблеме сети.
                Log.e("HotelApp", "Login error", e)
                errorMessage = AppRes.str(R.string.error_network)
            } finally {
                // В любом исходе снимаем индикатор загрузки.
                isLoading = false
            }
        }
    }

    /**
     * Регистрирует пользователя: проверяет имя, email и пароль (минимум 6 символов),
     * вызывает API и сохраняет токен. Ошибки 400/409 трактуются как «email занят».
     */
    fun register(fullName: String, email: String, password: String) {
        // Имя обязательно — без него дальше не идём.
        if (fullName.isBlank()) {
            errorMessage = AppRes.str(R.string.error_enter_full_name)
            return
        }
        // Проверяем email и пароль (для регистрации минимум 6 символов).
        if (!validate(email = email, password = password, minPassword = 6)) return
        // Сетевой вызов в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор загрузки и сбрасываем прошлую ошибку.
            isLoading = true
            errorMessage = null
            try {
                // Отправляем запрос регистрации (имя и email обрезаем от пробелов).
                val response = authApi.register(
                    RegisterRequest(fullName.trim(), email.trim(), password)
                )
                // Сохраняем токен и данные пользователя.
                TokenManager.save(response)
                // Поднимаем флаг успеха — экран по нему перейдёт дальше.
                isAuthenticated = true
            } catch (e: HttpException) {
                // Ответ-ошибка от сервера: 400/409 — email уже занят, иначе общий сбой регистрации.
                errorMessage = if (e.code() == 409 || e.code() == 400) {
                    AppRes.str(R.string.error_email_taken)
                } else {
                    AppRes.str(R.string.error_register_failed, e.code())
                }
            } catch (e: Exception) {
                // До сервера не дошли — сообщение о проблеме сети.
                Log.e("HotelApp", "Register error", e)
                errorMessage = AppRes.str(R.string.error_network)
            } finally {
                // В любом исходе снимаем индикатор загрузки.
                isLoading = false
            }
        }
    }

    /** Сбрасывает текст ошибки после того, как UI его показал. */
    fun consumeError() {
        errorMessage = null
    }

    /**
     * Проверяет корректность email и минимальную длину пароля.
     * При ошибке выставляет [errorMessage] и возвращает false.
     */
    private fun validate(email: String, password: String, minPassword: Int = 1): Boolean {
        // Email должен быть непустым и содержать «@».
        if (email.isBlank() || !email.contains("@")) {
            errorMessage = AppRes.str(R.string.error_invalid_email)
            return false
        }
        // Пароль должен быть не короче минимально допустимой длины.
        if (password.length < minPassword) {
            // При требуемой длине > 1 показываем минимум, иначе — просто «введите пароль».
            errorMessage = if (minPassword > 1) {
                AppRes.str(R.string.error_password_min, minPassword)
            } else {
                AppRes.str(R.string.error_enter_password)
            }
            return false
        }
        // Все проверки пройдены.
        return true
    }
}
