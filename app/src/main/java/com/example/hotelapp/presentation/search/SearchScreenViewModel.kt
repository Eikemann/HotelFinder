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

    var filterState by mutableStateOf(FilterState())
        private set

    val filteredHotels: List<Hotel>
        get() {
            var result = hotels

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
                result = result.filter { it.accommodationType in types }
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

    fun applyFilter(newFilter: FilterState) {
        filterState = newFilter
    }

    fun resetFilters() {
        filterState = FilterState()
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
