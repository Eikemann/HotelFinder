package com.example.hotelapp

import android.app.Application
import com.example.hotelapp.data.auth.TokenManager

class HotelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TokenManager.init(this)
    }
}
