package com.example.hotelapp.data.remote.api

import com.example.hotelapp.data.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/** Добавляет заголовок `Authorization: Bearer <token>` к исходящим запросам, если есть токен. */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = TokenManager.token
        // Нет токена или заголовок уже задан вручную — пропускаем запрос без изменений.
        if (token.isNullOrBlank() || original.header("Authorization") != null) {
            return chain.proceed(original)
        }
        val authed = original.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authed)
    }
}
