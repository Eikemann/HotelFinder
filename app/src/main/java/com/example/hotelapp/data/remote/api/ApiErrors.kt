package com.example.hotelapp.data.remote.api

import com.google.gson.Gson
import retrofit2.HttpException

/** Извлекает поле `message` из тела ошибки бэкенда (`ErrorResponse`), если оно есть. */
fun HttpException.serverMessage(): String? = try {
    val body = response()?.errorBody()?.string()
    if (body.isNullOrBlank()) {
        null
    } else {
        val map = Gson().fromJson(body, Map::class.java)
        map?.get("message") as? String
    }
} catch (e: Exception) {
    null
}
