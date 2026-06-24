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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardScreenViewModel : ViewModel() {

    private val hotelApi = RetrofitHelper.hotelApi

    companion object {
        // Preferred carousel order — the cities Russian tourists most travel to.
        // Values match the backend `city` field exactly.
        val CITY_ORDER = listOf(
            "Antalya", "Istanbul", "Dubai", "Hurghada", "Bangkok",
            "Phuket", "Tbilisi", "Batumi", "Goa", "Nha Trang"
        )
    }

    var hotels by mutableStateOf<List<Hotel>>(emptyList())
        private set

    /**
     * Hotels grouped into one carousel per city, ordered by [CITY_ORDER].
     * Cities not in the preferred order (and blanks) fall to the end.
     *
     * Computed once whenever [hotels] is (re)loaded — see [groupByCity] — rather
     * than re-derived on every recomposition, which previously caused dashboard
     * jank when tapping filters or scrolling.
     */
    var hotelsByCity by mutableStateOf<List<Pair<String, List<Hotel>>>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    private var hasLoaded = false

    fun loadHotels() {
        // Avoid re-fetching (and the spinner) on every navigation back to the tab.
        if (hasLoaded || isLoading) return
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val grouped = withContext(Dispatchers.IO) {
                    val loaded = hotelApi.getHotels().map { it.toHotel() }
                    loaded to groupByCity(loaded)
                }
                hotels = grouped.first
                hotelsByCity = grouped.second
                hasLoaded = true
                Log.d("HotelApp", "Hotels fetched from API")
            } catch (e: Exception) {
                Log.e("HotelApp", "Error fetching hotels", e)
                errorMessage = AppRes.str(R.string.error_offline_data)
                hotels = sampleHotels
                hotelsByCity = groupByCity(sampleHotels)
            } finally {
                isLoading = false
            }
        }
    }

    private fun groupByCity(list: List<Hotel>): List<Pair<String, List<Hotel>>> =
        list.filter { it.city.isNotBlank() }
            .groupBy { it.city }
            .toList()
            .sortedBy { (city, _) ->
                CITY_ORDER.indexOf(city).takeIf { it >= 0 } ?: Int.MAX_VALUE
            }
}
