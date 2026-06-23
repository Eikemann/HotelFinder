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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchScreenViewModel : ViewModel() {

    private val hotelApi = RetrofitHelper.hotelApi

    var hotels by mutableStateOf<List<Hotel>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var searchQuery by mutableStateOf("")
        private set

    // Set when arriving from a dashboard "See all" — restricts results to one city.
    var cityFilter by mutableStateOf("")
        private set

    var filterState by mutableStateOf(FilterState())
        private set

    // Distinct countries present in the loaded data (the country part of "City, Country"),
    // so the filter only offers options that can actually return results.
    val availableCountries: List<String>
        get() = hotels.mapNotNull { it.location.substringAfter(", ", "").takeIf(String::isNotBlank) }
            .distinct()
            .sorted()

    // Distinct accommodation types in the data (backend enum values: HOTEL/APARTMENT/...).
    val availableTypes: List<String>
        get() = hotels.map { it.accommodationType }.filter { it.isNotBlank() }.distinct().sorted()

    // Highest nightly price in the data, rounded up to a sensible slider ceiling.
    val priceCeiling: Float
        get() = hotels.mapNotNull { it.pricePerNight.toFloatOrNull() }.maxOrNull()
            ?.let { kotlin.math.ceil(it / 100f) * 100f }
            ?.coerceAtLeast(100f)
            ?: 1000f

    val filteredHotels: List<Hotel>
        get() {
            var result = hotels

            if (cityFilter.isNotBlank()) {
                result = result.filter { it.city.equals(cityFilter, ignoreCase = true) }
            }

            if (searchQuery.isNotBlank()) {
                result = result.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.location.contains(searchQuery, ignoreCase = true)
                }
            }

            filterState.country?.let { country ->
                result = result.filter { it.location.contains(country, ignoreCase = true) }
            }

            filterState.accommodationTypes.takeIf { it.isNotEmpty() }?.let { types ->
                result = result.filter { hotel ->
                    types.any { it.equals(hotel.accommodationType, ignoreCase = true) }
                }
            }

            // Each selected facility is a keyword that must appear in at least one amenity (AND across selections).
            filterState.facilities.takeIf { it.isNotEmpty() }?.let { keywords ->
                result = result.filter { hotel ->
                    keywords.all { kw -> hotel.amenities.any { it.contains(kw, ignoreCase = true) } }
                }
            }

            filterState.starRating?.let { rating ->
                result = result.filter { (it.rating.toFloatOrNull() ?: 0f) >= rating }
            }

            result = result.filter {
                val price = it.pricePerNight.toFloatOrNull() ?: 0f
                price >= filterState.priceRange.start && price <= filterState.priceRange.endInclusive
            }

            result = when (filterState.sort) {
                "Highest Price" -> result.sortedByDescending { it.pricePerNight.toFloatOrNull() ?: 0f }
                "Lowest Price" -> result.sortedBy { it.pricePerNight.toFloatOrNull() ?: 0f }
                else -> result
            }

            return result
        }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    fun applyCityFilter(city: String) {
        cityFilter = city
    }

    fun applyFilter(newFilter: FilterState) {
        filterState = newFilter
    }

    fun resetFilters() {
        filterState = FilterState(priceRange = 0f..priceCeiling)
    }

    fun loadHotels() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO) {
                    hotelApi.getHotels()
                }
                hotels = response.map { it.toHotel() }
                // Open the price slider up to the most expensive hotel so nothing is
                // hidden by a stale 1000-default ceiling, unless the user already filtered.
                if (filterState == FilterState()) {
                    filterState = FilterState(priceRange = 0f..priceCeiling)
                }
                Log.d("HotelApp", "Hotels fetched from API")
            } catch (e: Exception) {
                Log.e("HotelApp", "Error fetching hotels", e)
                errorMessage = AppRes.str(R.string.error_offline_data)
                hotels = sampleHotels
            } finally {
                isLoading = false
            }
        }
    }
}
