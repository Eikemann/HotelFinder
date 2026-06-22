package com.example.hotelapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.hotelapp.data.AppRes
import com.example.hotelapp.data.auth.TokenManager
import com.example.hotelapp.data.remote.api.RetrofitHelper

class HotelApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        AppRes.appContext = this
        TokenManager.init(this)
        // Pick the reachable backend (local Docker, else hosted Railway) before the
        // first API call. The probe does socket I/O so it runs on a background
        // thread; we join briefly so the chosen URL is ready when screens load.
        Thread { RetrofitHelper.resolveBaseUrl() }.apply { start(); join(1500) }
    }

    // App-wide Coil loader: soft crossfade on image load + bounded caches so
    // list images come back instantly when scrolling.
    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(64L * 1024 * 1024)
                    .build()
            }
            .build()
}
