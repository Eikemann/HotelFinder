package com.example.hotelapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.hotelapp.data.AppRes
import com.example.hotelapp.data.auth.TokenManager

class HotelApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        AppRes.appContext = this
        TokenManager.init(this)
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
