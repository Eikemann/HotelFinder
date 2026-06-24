package com.example.hotelapp.presentation.search

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.R
import com.example.hotelapp.data.AppRes
import com.example.hotelapp.data.sampleHotels
import com.example.hotelapp.data.mapper.toHotel
import com.example.hotelapp.data.remote.api.HotelApi
import com.example.hotelapp.data.remote.api.RetrofitHelper
import com.example.hotelapp.domain.local.model.FilterState
import com.example.hotelapp.domain.local.model.Hotel
import com.example.hotelapp.presentation.components.HotelSearchThumbSize
import com.example.hotelapp.presentation.components.hotelImageRequest
import coil.imageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel экрана поиска: загружает отели и применяет к ним поисковый запрос,
 * фильтры (город, страна, тип, удобства, рейтинг, цена) и сортировку.
 * Также вычисляет доступные варианты фильтров на основе загруженных данных.
 */
class SearchScreenViewModel : ViewModel() {

    private val hotelApi = RetrofitHelper.hotelApi

    // --- Состояние UI ---

    var hotels by mutableStateOf<List<Hotel>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    // Устанавливается при переходе с главного экрана по «Смотреть все» — ограничивает выдачу одним городом.
    var cityFilter by mutableStateOf("")
        private set

    var filterState by mutableStateOf(FilterState())
        private set

    // --- Производные варианты фильтров (вычисляются по загруженным данным) ---

    // Уникальные страны из данных (часть «страна» из строки "Город, Страна"),
    // чтобы фильтр предлагал только варианты, которые реально что-то вернут.
    val availableCountries: List<String>
        get() = hotels.mapNotNull { it.location.substringAfter(", ", "").takeIf(String::isNotBlank) }
            .distinct()
            .sorted()

    // Уникальные типы размещения из данных (значения enum бэкенда: HOTEL/APARTMENT/...).
    val availableTypes: List<String>
        get() = hotels.map { it.accommodationType }.filter { it.isNotBlank() }.distinct().sorted()

    // Максимальная цена за ночь в данных, округлённая вверх до разумного потолка слайдера.
    val priceCeiling: Float
        get() = hotels.mapNotNull { it.pricePerNight.toFloatOrNull() }.maxOrNull()
            ?.let { kotlin.math.ceil(it / 100f) * 100f }
            ?.coerceAtLeast(100f)
            ?: 1000f

    /**
     * Итоговая выдача: последовательно применяет к [hotels] фильтр города, поисковый
     * запрос (имя/местоположение), страну, тип, удобства, рейтинг и диапазон цены,
     * затем сортировку. Каждый шаг сужает предыдущий результат.
     */
    val filteredHotels: List<Hotel>
        get() {
            // Начинаем с полного списка и постепенно сужаем его каждым фильтром.
            var result = hotels

            // Фильтр по конкретному городу (переход из «Смотреть все»).
            if (cityFilter.isNotBlank()) {
                result = result.filter { it.city.equals(cityFilter, ignoreCase = true) }
            }

            // Поиск по названию или местоположению.
            if (searchQuery.isNotBlank()) {
                result = result.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
                }
            }

            // Фильтр по стране.
            filterState.country?.let { country ->
                result = result.filter { it.location.contains(country, ignoreCase = true) }
            }

            // Фильтр по типам размещения (совпадение с любым из выбранных).
            filterState.accommodationTypes.takeIf { it.isNotEmpty() }?.let { types ->
                result = result.filter { hotel ->
                    types.any { it.equals(hotel.accommodationType, ignoreCase = true) }
                }
            }

            // Каждое выбранное удобство — ключевое слово, которое должно встретиться хотя бы
            // в одном из amenities отеля (логическое И по всем выбранным).
            filterState.facilities.takeIf { it.isNotEmpty() }?.let { keywords ->
                result = result.filter { hotel ->
                    keywords.all { kw -> hotel.amenities.any { it.contains(kw, ignoreCase = true) } }
                }
            }

            // Фильтр по минимальному рейтингу.
            filterState.starRating?.let { rating ->
                result = result.filter { (it.rating.toFloatOrNull() ?: 0f) >= rating }
            }

            // Фильтр по диапазону цены за ночь.
            result = result.filter {
                val price = it.pricePerNight.toFloatOrNull() ?: 0f
                price >= filterState.priceRange.start && price <= filterState.priceRange.endInclusive
            }

            // Сортировка по цене (по убыванию/возрастанию) либо без сортировки.
            result = when (filterState.sort) {
                "Highest Price" -> result.sortedByDescending { it.pricePerNight.toFloatOrNull() ?: 0f }
                "Lowest Price" -> result.sortedBy { it.pricePerNight.toFloatOrNull() ?: 0f }
                else -> result
            }

            // Возвращаем отфильтрованный и отсортированный список.
            return result
        }

    /** Обновляет поисковый запрос. */
    fun onSearchQueryChange(query: String) {
        // Запоминаем новый текст поиска — выдача пересчитается автоматически.
        searchQuery = query
    }

    /** Ограничивает выдачу одним городом (используется при переходе из «Смотреть все»). */
    fun applyCityFilter(city: String) {
        // Запоминаем город-ограничитель выдачи.
        cityFilter = city
    }

    /** Применяет новый набор фильтров. */
    fun applyFilter(newFilter: FilterState) {
        // Заменяем состояние фильтров на пришедшее из нижнего листа.
        filterState = newFilter
    }

    /** Сбрасывает фильтры к значениям по умолчанию (с диапазоном цены до текущего потолка). */
    fun resetFilters() {
        // Возвращаем фильтры к значениям по умолчанию, оставляя цену в пределах текущего потолка.
        filterState = FilterState(priceRange = 0f..priceCeiling)
    }

    /**
     * Прогревает кэш Coil для изображений списка поиска тем же запросом (размер + rgb565),
     * что и [HotelImage] в HotelCard, чтобы карточки рисовались мгновенно.
     * Ограничено первым экраном, чтобы не перегрузить загрузчик.
     */
    private fun preloadThumbnails(list: List<Hotel>) {
        // Берём контекст приложения и общий загрузчик изображений Coil.
        val ctx = AppRes.appContext
        val loader = ctx.imageLoader
        // Берём первые 20 непустых URL и ставим их в очередь предзагрузки
        // тем же запросом, что и карточки списка — ради совпадения кэша.
        list.asSequence()
            .mapNotNull { it.imageUrl }
            .take(20)
            .forEach { url -> loader.enqueue(hotelImageRequest(ctx, url, HotelSearchThumbSize)) }
    }

    /** Загружает отели; при ошибке подставляет офлайн-данные [sampleHotels]. */
    fun loadHotels() {
        // Загрузку выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор загрузки и сбрасываем прошлую ошибку.
            isLoading = true
            errorMessage = null
            try {
                // Запрашиваем отели в фоновом потоке (IO).
                val response = withContext(Dispatchers.IO) {
                    hotelApi.getHotels()
                }
                // Маппим сетевые модели в доменные.
                hotels = response.map { it.toHotel() }
                // Раздвигаем слайдер цены до самого дорогого отеля, чтобы ничего не скрывалось
                // потолком по умолчанию (1000), если пользователь ещё не настраивал фильтры.
                if (filterState == FilterState()) {
                    filterState = FilterState(priceRange = 0f..priceCeiling)
                }
                // Прогреваем кэш изображений для плавной прокрутки.
                preloadThumbnails(hotels)
                Log.d("HotelApp", "Hotels fetched from API")
            } catch (e: Exception) {
                // Ошибка сети: уведомление и офлайн-данные.
                Log.e("HotelApp", "Error fetching hotels", e)
                errorMessage = AppRes.str(R.string.error_offline_data)
                hotels = sampleHotels
            } finally {
                // В любом исходе снимаем индикатор загрузки.
                isLoading = false
            }
        }
    }
}
