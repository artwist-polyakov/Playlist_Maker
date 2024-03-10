package com.example.playlistmaker.common.presentation

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.TypedValue

fun Context.getThemeColor(attr: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attr, typedValue, true)
    return typedValue.data
}

/**
 * Проверяет доступность интернета, возвращает:
 * - true — если интернет доступен,
 * - false — если интернет недоступен.
 *
 * Пример вызова: requireContext().checkInternetReachability()
 *
 *  Эта функция работает следующим образом:
 *  1. Получает сервис системы CONNECTIVITY_SERVICE, который предоставляет информацию о состоянии сети.
 *  2. Проверяет, есть ли активная сеть. Если нет, возвращает false.
 *  3. Получает возможности активной сети. Если их нет, возвращает false.
 *  4. Проверяет, доступен ли интернет через Wi-Fi или мобильную сеть. Если да, возвращает true.
 *  5. Если интернет не доступен ни через Wi-Fi, ни через мобильную сеть, возвращает false.
 */
fun Context.checkInternetReachability(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork =
        connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) } ?: return false
    return when {
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        else -> false
    }
}