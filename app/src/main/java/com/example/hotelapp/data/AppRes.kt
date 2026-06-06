package com.example.hotelapp.data

import android.content.Context

/**
 * Lightweight holder for the application context so non-Composable code
 * (ViewModels) can resolve localized string resources. Initialized in
 * [com.example.hotelapp.HotelApplication].
 */
object AppRes {
    lateinit var appContext: Context

    fun str(resId: Int): String = appContext.getString(resId)

    fun str(resId: Int, vararg args: Any): String = appContext.getString(resId, *args)
}
