package com.example.playlistmaker.common.presentation

import android.content.Context
import android.content.IntentFilter
import android.net.ConnectivityManager

class ComposeInternetConnectionBroadcastReceiver(
    private val onInternetUnavailable: () -> Unit,
    private val onInternetAvailable: () -> Unit
) {
    private val receiver = InternetConnectionBroadcastReciever(
        action_internet_unavailabla = onInternetUnavailable,
        action_internet_available = onInternetAvailable
    )

    fun register(context: Context) {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(receiver, filter)
    }

    fun unregister(context: Context) {
        context.unregisterReceiver(receiver)
    }
}