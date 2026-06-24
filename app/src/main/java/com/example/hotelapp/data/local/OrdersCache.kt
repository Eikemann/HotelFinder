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
 * Хранит брони пользователя локально (в виде JSON), чтобы показывать их офлайн.
 * Использует DataStore по аналогии с [com.example.hotelapp.data.auth.TokenManager].
 * Обновляется при каждой успешной загрузке; очищается при выходе, чтобы брони
 * одного аккаунта не попали к другому.
 */
object OrdersCache {

    private val KEY_ORDERS = stringPreferencesKey("my_orders_json")
    private val gson = Gson()
    private val listType = object : TypeToken<List<OrderResponse>>() {}.type

    private val context: Context get() = AppRes.appContext

    /** Сохраняет последние брони, полученные с сервера. */
    suspend fun save(orders: List<OrderResponse>) {
        try {
            val json = gson.toJson(orders, listType)
            context.ordersDataStore.edit { it[KEY_ORDERS] = json }
        } catch (e: Exception) {
            Log.e("HotelApp", "Failed to cache orders", e)
        }
    }

    /** Возвращает закэшированные брони или пустой список, если их нет/не удалось прочитать. */
    suspend fun load(): List<OrderResponse> {
        return try {
            val json = context.ordersDataStore.data.first()[KEY_ORDERS] ?: return emptyList()
            gson.fromJson<List<OrderResponse>>(json, listType) ?: emptyList()
        } catch (e: Exception) {
            Log.e("HotelApp", "Failed to read cached orders", e)
            emptyList()
        }
    }

    /** Удаляет закэшированные брони (вызывается при выходе из аккаунта). */
    suspend fun clear() {
        context.ordersDataStore.edit { it.remove(KEY_ORDERS) }
    }
}
