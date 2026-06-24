package com.example.hotelapp.data.remote.api

import android.os.Build
import com.example.hotelapp.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.InetSocketAddress
import java.net.Socket
import java.net.URL

/**
 * Единая точка сборки Retrofit/OkHttp и кэш API-интерфейсов.
 * Выбирает адрес бэкенда при старте и предоставляет общие экземпляры API всем ViewModel.
 */
object RetrofitHelper {

    // ---------------------------------------------------------------------------------------------
    // Выбор адреса бэкенда
    // ---------------------------------------------------------------------------------------------

    // Кандидаты бэкенда в порядке приоритета: сначала локальный Docker, затем хостинг Railway
    // как запасной вариант. Какой использовать — решает resolveBaseUrl() при старте.
    private val localUrl: String = BuildConfig.LOCAL_BASE_URL
    private val remoteUrl: String = BuildConfig.REMOTE_BASE_URL

    private const val CONNECT_TIMEOUT_MS = 100

    // Выбранный адрес. По умолчанию — удалённый, чтобы при любой неопределённости использовать
    // публичный URL, работающий на реальных устройствах; resolveBaseUrl() может переключить
    // на локальный для разработки в эмуляторе. Читается ленивым Retrofit ниже, поэтому должен
    // быть определён до первого API-вызова (см. HotelApplication.onCreate).
    @Volatile
    var baseUrl: String = remoteUrl
        private set

    // Локальный URL (10.0.2.2) — это петля «эмулятор → хост» и бессмыслен на реальном телефоне,
    // где его проверка может зависнуть или ложно сработать на устройство в локальной сети. Поэтому
    // локальный адрес рассматриваем только в эмуляторе, иначе используем хостинг. Делает блокирующий
    // сетевой ввод-вывод, поэтому ДОЛЖЕН вызываться вне главного потока (Android запрещает сеть в нём).
    fun resolveBaseUrl() {
        baseUrl = if (isEmulator() && isReachable(localUrl)) localUrl else remoteUrl
    }

    /** Эвристически определяет, запущено ли приложение в эмуляторе (по свойствам сборки). */
    private fun isEmulator(): Boolean =
        Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.contains("emulator", ignoreCase = true) ||
            Build.MODEL.contains("Emulator", ignoreCase = true) ||
            Build.MODEL.contains("Android SDK built for", ignoreCase = true) ||
            Build.PRODUCT.contains("sdk", ignoreCase = true) ||
            Build.HARDWARE.contains("goldfish") ||
            Build.HARDWARE.contains("ranchu")

    /** Пытается открыть TCP-соединение с адресом за отведённый таймаут; true — если доступен. */
    private fun isReachable(url: String): Boolean = try {
        val parsed = URL(url)
        val port = if (parsed.port != -1) parsed.port else parsed.defaultPort
        Socket().use { socket ->
            socket.connect(InetSocketAddress(parsed.host, port), CONNECT_TIMEOUT_MS)
            true
        }
    } catch (e: Exception) {
        false
    }

    // ---------------------------------------------------------------------------------------------
    // Сборка Retrofit / OkHttp
    // ---------------------------------------------------------------------------------------------

    // OkHttp-клиент с интерцептором авторизации и логированием тела запросов только в debug-сборке.
    private val client: OkHttpClient by lazy {
        val logging = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor())
            .addInterceptor(logging)
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            // GsonConverterFactory преобразует JSON-ответ в объекты Kotlin.
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /** Возвращает общий экземпляр Retrofit. */
    fun getInstance(): Retrofit = retrofit

    // ---------------------------------------------------------------------------------------------
    // Общие экземпляры API
    // ---------------------------------------------------------------------------------------------

    // Прокси Retrofit создаются один раз и переиспользуются всеми ViewModel,
    // а не пересоздаются для каждого экрана.
    val hotelApi: HotelApi by lazy { retrofit.create(HotelApi::class.java) }
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val roomApi: RoomApi by lazy { retrofit.create(RoomApi::class.java) }
    val orderApi: OrderApi by lazy { retrofit.create(OrderApi::class.java) }
    val reviewApi: ReviewApi by lazy { retrofit.create(ReviewApi::class.java) }
}
