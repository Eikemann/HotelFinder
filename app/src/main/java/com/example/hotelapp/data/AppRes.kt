package com.example.hotelapp.data

import android.content.Context

/**
 * Лёгкий держатель контекста приложения, чтобы не-Composable код (ViewModel) мог
 * получать локализованные строковые ресурсы. Инициализируется в
 * [com.example.hotelapp.HotelApplication].
 */
object AppRes {
    lateinit var appContext: Context

    /** Возвращает строковый ресурс по его id. */
    fun str(resId: Int): String = appContext.getString(resId)

    /** Возвращает форматированный строковый ресурс с подстановкой аргументов. */
    fun str(resId: Int, vararg args: Any): String = appContext.getString(resId, *args)
}
