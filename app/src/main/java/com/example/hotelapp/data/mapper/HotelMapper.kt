package com.example.hotelapp.data.mapper

import com.example.hotelapp.domain.local.model.Hotel
import com.example.hotelapp.domain.remote.model.HotelResponseItem

/**
 * Преобразует сетевую модель [HotelResponseItem] в доменную [Hotel] для UI:
 * собирает отображаемое местоположение, форматирует цену и выбирает рейтинг.
 */
fun HotelResponseItem.toHotel(): Hotel {
    return Hotel(
        id = id,
        name = name,
        city = city.orEmpty(),
        // Бэкенд хранит части адреса отдельно — собираем строку местоположения для отображения.
        location = listOfNotNull(city, country)
            .filter { it.isNotBlank() }
            .joinToString(", "),
        description = description.orEmpty(),
        // Цена «от» — самая дешёвая комната объекта (может быть null, если комнат нет).
        pricePerNight = pricePerNight?.let { "%.0f".format(it) }.orEmpty(),
        // Предпочитаем рейтинг по отзывам; при его отсутствии берём звёздность.
        rating = (rating ?: starRating?.toDouble())?.toString().orEmpty(),
        imageRes = 0,
        accommodationType = propertyType.orEmpty(),
        imageUrl = imageUrl,
        amenities = amenities.map { it.name }
    )
}
