package com.example.hotelapp.presentation.dashboard

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
import com.example.hotelapp.domain.local.model.Hotel
import com.example.hotelapp.presentation.components.HotelThumbSize
import com.example.hotelapp.presentation.components.hotelImageRequest
import coil.imageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel главного экрана: загружает отели, группирует их в карусели по городам
 * и предзагружает миниатюры. При сбое сети показывает офлайн-данные [sampleHotels].
 */
class DashboardScreenViewModel : ViewModel() {

    private val hotelApi = RetrofitHelper.hotelApi

    companion object {
        // Предпочтительный порядок каруселей — города, куда чаще всего ездят туристы.
        // Значения точно совпадают с полем `city` на бэкенде.
        val CITY_ORDER = listOf(
            "Antalya", "Istanbul", "Dubai", "Hurghada", "Bangkok",
            "Phuket", "Tbilisi", "Batumi", "Goa", "Nha Trang"
        )
    }

    var hotels by mutableStateOf<List<Hotel>>(emptyList())
        private set

    /**
     * Отели, сгруппированные в карусели по городам в порядке [CITY_ORDER].
     * Города не из списка предпочтений (и пустые) уходят в конец.
     *
     * Вычисляется один раз при каждой (пере)загрузке [hotels] — см. [groupByCity], —
     * а не на каждой рекомпозиции, что раньше вызывало подтормаживания экрана
     * при нажатии фильтров или прокрутке.
     */
    var hotelsByCity by mutableStateOf<List<Pair<String, List<Hotel>>>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var hasLoaded = false

    /**
     * Загружает отели и формирует карусели по городам. Кэширует результат, чтобы
     * не перезапрашивать при каждом возврате на вкладку; при ошибке — офлайн-данные.
     */
    fun loadHotels() {
        // Не перезапрашиваем (и не показываем спиннер) при каждом возврате на вкладку.
        if (hasLoaded || isLoading) return
        // Загрузку выполняем в корутине, привязанной к жизни ViewModel.
        viewModelScope.launch {
            // Включаем индикатор загрузки и сбрасываем прошлую ошибку.
            isLoading = true
            errorMessage = null
            try {
                // В фоновом потоке (IO) загружаем отели, маппим в доменную модель
                // и сразу группируем по городам — отдаём парой (список, группы).
                val grouped = withContext(Dispatchers.IO) {
                    val loaded = hotelApi.getHotels().map { it.toHotel() }
                    loaded to groupByCity(loaded)
                }
                // Кладём в состояние плоский список и карусели по городам.
                hotels = grouped.first
                hotelsByCity = grouped.second
                // Помечаем, что данные уже загружены (чтобы не перезапрашивать).
                hasLoaded = true
                // Прогреваем кэш изображений для плавной прокрутки.
                preloadThumbnails(grouped.first)
                Log.d("HotelApp", "Hotels fetched from API")
            } catch (e: Exception) {
                // Ошибка сети: уведомление и офлайн-данные (тоже сгруппированные).
                Log.e("HotelApp", "Error fetching hotels", e)
                errorMessage = AppRes.str(R.string.error_offline_data)
                hotels = sampleHotels
                hotelsByCity = groupByCity(sampleHotels)
            } finally {
                // В любом исходе снимаем индикатор загрузки.
                isLoading = false
            }
        }
    }

    /**
     * Прогревает кэш Coil (память/диск) для изображений каруселей, чтобы карточки
     * появлялись мгновенно при прокрутке. Использует ТОТ ЖЕ запрос (размер + rgb565),
     * что и [HotelImage] в карусели, чтобы ключи кэша совпадали; ограничено по числу,
     * чтобы не перегрузить загрузчик и не вытеснить записи до показа.
     */
    private fun preloadThumbnails(list: List<Hotel>) {
        // Берём контекст приложения и общий загрузчик изображений Coil.
        val ctx = AppRes.appContext
        val loader = ctx.imageLoader
        // Берём первые 24 непустых URL и ставим их в очередь предзагрузки
        // тем же запросом (размер + rgb565), что и карусель — ради совпадения кэша.
        list.asSequence()
            .mapNotNull { it.imageUrl }
            .take(24)
            .forEach { url -> loader.enqueue(hotelImageRequest(ctx, url, HotelThumbSize)) }
    }

    /** Группирует отели по городу и сортирует группы согласно [CITY_ORDER]. */
    private fun groupByCity(list: List<Hotel>): List<Pair<String, List<Hotel>>> =
        // Отбрасываем отели без города, группируем по городу, превращаем в список пар
        // и сортируем по позиции города в CITY_ORDER (отсутствующие — в конец).
        list.filter { it.city.isNotBlank() }
            .groupBy { it.city }
            .toList()
            .sortedBy { (city, _) ->
                CITY_ORDER.indexOf(city).takeIf { it >= 0 } ?: Int.MAX_VALUE
            }
}
