package com.example.hotelapp.data.remote.api

import com.example.hotelapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

object RetrofitHelper {

    // Backend candidates, tried in order: local Docker first, hosted Railway as a
    // fallback. resolveBaseUrl() decides which one to use at startup.
    private val localUrl: String = BuildConfig.LOCAL_BASE_URL
    private val remoteUrl: String = BuildConfig.REMOTE_BASE_URL

    private const val CONNECT_TIMEOUT_MS = 800

    // The chosen backend. Defaults to local; resolveBaseUrl() may switch it to the
    // remote fallback. Read by the lazy Retrofit instance below, so this must be
    // resolved before the first API call (see HotelApplication.onCreate).
    @Volatile
    var baseUrl: String = localUrl
        private set

    // Probe the local backend and fall back to the hosted one if the emulator
    // can't reach it. Does blocking socket I/O, so it MUST be called off the main
    // thread (Android forbids network on the main thread).
    fun resolveBaseUrl() {
        baseUrl = if (isReachable(localUrl)) localUrl else remoteUrl
    }

    private fun isReachable(url: String): Boolean = try {
        val parsed = URL(url)
        val port = if (parsed.port != -1) parsed.port else parsed.defaultPort
        Socket().use { socket ->
            socket.connect(InetSocketAddress(parsed.host, port), CONNECT_TIMEOUT_MS)
            true
        }
    } catch (e: Exception) {
        false
    }

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
