package com.example.playlistmaker.common.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class InternetConnectionBroadcastReciever : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == CONNECTIVITY_ACTION) {
            context?.checkInternetReachability().let {
                if (it != true) {
                    Toast.makeText(
                        context,
                        "Отсутствует подключение к интернету",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }


    companion object {
        const val CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE"
    }


}