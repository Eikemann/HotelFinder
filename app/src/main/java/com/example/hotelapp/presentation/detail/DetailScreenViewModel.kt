package com.example.hotelapp.presentation.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hotelapp.data.sampleHotels
import com.example.hotelapp.data.mapper.toHotel
import com.example.hotelapp.data.remote.api.HotelApi
import com.example.hotelapp.data.remote.api.RetrofitHelper
import com.example.hotelapp.domain.local.model.Hotel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailScreenViewModel : ViewModel() {

    private val hotelApi = RetrofitHelper.getInstance().create(HotelApi::class.java)

    var hotel by mutableStateOf<Hotel?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun loadHotelById(hotelId: Int) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = withContext(Dispatchers.IO) {
                    hotelApi.getHotelsById(hotelId)
                }
                hotel = response.toHotel()
                Log.d("HotelApp", "Hotel $hotelId fetched from API")
            } catch (e: Exception) {
                Log.e("HotelApp", "Error fetching hotel $hotelId", e)
                errorMessage = "Could not connect to server. Showing offline data."
                hotel = sampleHotels.find { it.id == hotelId }
            } finally {
                isLoading = false
            }
        }
    }
}
