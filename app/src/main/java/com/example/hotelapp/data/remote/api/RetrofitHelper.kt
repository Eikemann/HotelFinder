package com.example.hotelapp.data.remote.api

import com.example.hotelapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {

    val baseUrl: String = BuildConfig.BASE_URL

    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            // GsonConverterFactory converts the JSON response into Kotlin objects.
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getInstance(): Retrofit = retrofit

    // Shared API instances — Retrofit proxies are created once and reused by
    // every ViewModel instead of being rebuilt per screen.
    val hotelApi: HotelApi by lazy { retrofit.create(HotelApi::class.java) }
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val roomApi: RoomApi by lazy { retrofit.create(RoomApi::class.java) }
    val orderApi: OrderApi by lazy { retrofit.create(OrderApi::class.java) }
    val reviewApi: ReviewApi by lazy { retrofit.create(ReviewApi::class.java) }
}
