package com.example.hotelapp.data.remote.api

import com.google.gson.Gson
import retrofit2.HttpException

/** Extracts the `message` field from a backend `ErrorResponse` body, if present. */
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
