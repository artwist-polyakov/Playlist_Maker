package com.example.playlistmaker.common.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class InternetConnectionBroadcastReciever(
    val action_internet_unavailabla: () -> Unit = { },
    val action_internet_available: () -> Unit = { }
) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == CONNECTIVITY_ACTION) {
            context?.checkInternetReachability().let {
                when (it) {
                    true -> action_internet_available()
                    false -> action_internet_unavailabla()
                    null -> {
                    }
                }
            }
        }
    }


    companion object {
        const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }


}