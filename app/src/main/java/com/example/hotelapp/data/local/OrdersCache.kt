package com.example.hotelapp.data.local

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hotelapp.data.AppRes
import com.example.hotelapp.domain.remote.model.booking.OrderResponse
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.first

private val Context.ordersDataStore by preferencesDataStore(name = "orders_prefs")

/**
 * Persists the user's bookings locally (as JSON) so they can still be shown when
 * the device is offline. Backed by DataStore, mirroring [com.example.hotelapp.data.auth.TokenManager].
 * Refreshed on every successful load; cleared on logout so one account's bookings
 * never leak to the next.
 */
object OrdersCache {

    private val KEY_ORDERS = stringPreferencesKey("my_orders_json")
    private val gson = Gson()
    private val listType = object : TypeToken<List<OrderResponse>>() {}.type

    private val context: Context get() = AppRes.appContext

    /** Stores the latest bookings fetched from the server. */
    suspend fun save(orders: List<OrderResponse>) {
        try {
            val json = gson.toJson(orders, listType)
            context.ordersDataStore.edit { it[KEY_ORDERS] = json }
        } catch (e: Exception) {
            Log.e("HotelApp", "Failed to cache orders", e)
        }
    }

    /** Returns the cached bookings, or an empty list if none/unreadable. */
    suspend fun load(): List<OrderResponse> {
        return try {
            val json = context.ordersDataStore.data.first()[KEY_ORDERS] ?: return emptyList()
            gson.fromJson<List<OrderResponse>>(json, listType) ?: emptyList()
        } catch (e: Exception) {
            Log.e("HotelApp", "Failed to read cached orders", e)
            emptyList()
        }
    }

    /** Drops the cached bookings (call on logout). */
    suspend fun clear() {
        context.ordersDataStore.edit { it.remove(KEY_ORDERS) }
    }
}
