package com.example.hotelapp.data.remote.api

import com.example.hotelapp.data.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

/** Attaches `Authorization: Bearer <token>` to outgoing requests when logged in. */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val token = TokenManager.token
        if (token.isNullOrBlank() || original.header("Authorization") != null) {
            return chain.proceed(original)
        }
        val authed = original.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()
        return chain.proceed(authed)
    }
}
