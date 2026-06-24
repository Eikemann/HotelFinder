package com.example.hotelapp

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.hotelapp.data.AppRes
import com.example.hotelapp.data.auth.TokenManager
import com.example.hotelapp.data.remote.api.RetrofitHelper

/**
 * Класс Application: точка ранней инициализации приложения. Прогревает контекст для
 * [AppRes], восстанавливает токен в [TokenManager], выбирает адрес бэкенда и настраивает
 * общий загрузчик изображений Coil.
 */
class HotelApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        AppRes.appContext = this
        TokenManager.init(this)
        // Выбираем доступный бэкенд (локальный Docker, иначе хостинг Railway) до первого
        // API-вызова. Проверка делает сетевой ввод-вывод, поэтому идёт в фоновом потоке;
        // кратко ждём её (join), чтобы к загрузке экранов адрес был уже выбран.
        Thread { RetrofitHelper.resolveBaseUrl() }.apply { start(); join(1500) }
    }

    /**
     * Общий для приложения загрузчик Coil: плавное появление изображений и ограниченные
     * кэши (память/диск), чтобы картинки списков мгновенно возвращались при прокрутке.
     */
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
