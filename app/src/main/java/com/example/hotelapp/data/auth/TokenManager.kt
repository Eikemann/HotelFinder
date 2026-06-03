package com.example.hotelapp.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.hotelapp.domain.remote.model.auth.AuthResponse
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

/**
 * Persists the JWT and basic user info via DataStore, and keeps an in-memory
 * cache so the OkHttp interceptor and the startup routing decision can read the
 * token synchronously. Must be [init]ialised once from [HotelApplication].
 */
object TokenManager {

    private val KEY_TOKEN = stringPreferencesKey("token")
    private val KEY_EMAIL = stringPreferencesKey("email")
    private val KEY_FULL_NAME = stringPreferencesKey("full_name")
    private val KEY_ROLE = stringPreferencesKey("role")

    private lateinit var appContext: Context

    @Volatile
    var token: String? = null
        private set

    @Volatile
    var email: String? = null
        private set

    @Volatile
    var fullName: String? = null
        private set

    val isLoggedIn: Boolean
        get() = !token.isNullOrBlank()

    fun init(context: Context) {
        appContext = context.applicationContext
        // One-time blocking read to warm the in-memory cache at app start.
        runBlocking {
            val prefs = appContext.authDataStore.data.first()
            token = prefs[KEY_TOKEN]
            email = prefs[KEY_EMAIL]
            fullName = prefs[KEY_FULL_NAME]
        }
    }

    suspend fun save(response: AuthResponse) {
        appContext.authDataStore.edit { prefs ->
            response.token?.let { prefs[KEY_TOKEN] = it }
            response.email?.let { prefs[KEY_EMAIL] = it }
            response.fullName?.let { prefs[KEY_FULL_NAME] = it }
            response.role?.let { prefs[KEY_ROLE] = it }
        }
        token = response.token
        email = response.email
        fullName = response.fullName
    }

    suspend fun clear() {
        clearCache()
        appContext.authDataStore.edit { it.clear() }
    }

    /** Synchronously clears the in-memory cache so callers see logged-out immediately. */
    fun clearCache() {
        token = null
        email = null
        fullName = null
    }
}
