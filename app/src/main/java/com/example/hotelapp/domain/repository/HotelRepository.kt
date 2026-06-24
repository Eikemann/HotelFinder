package com.example.hotelapp.domain.repository

/** Контракт репозитория отелей (заготовка для слоя доступа к данным). */
interface HotelRepository {

    /** Загружает список всех отелей. */
    suspend fun getAllHotels()

}