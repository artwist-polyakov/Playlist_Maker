package com.example.playlistmakerclient

import android.app.Activity
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.example.aidl.service.IAudioPlayerService
import com.example.aidl.service.buildAudioPlayerBindIntent

class MainActivity : Activity() {
    private var audioPlayerService: IAudioPlayerService? = null
    private var title: TextView? = null
    private var button: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = findViewById(R.id.title)
        button = findViewById(R.id.button_what)
        button?.setOnClickListener {
            connectToService()
        }
    }

    private fun connectToService() {
        val connection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                audioPlayerService = IAudioPlayerService.Stub.asInterface(service)
                try {
                    title?.text = audioPlayerService?.getTrackTitle()
                } catch (e: RemoteException) {
                    Log.e(LOGGING_TAG, e.message.orEmpty())
                    throw RuntimeException(e)
                }
            }

            override fun onServiceDisconnected(name: ComponentName) {
                audioPlayerService = null
            }
        }
        val bindingRequestedSuccessfully = bindService(
            buildAudioPlayerBindIntent(),
            connection,
            BIND_AUTO_CREATE
        )
        Log.i(LOGGING_TAG, "Binding requested successfully: $bindingRequestedSuccessfully")
    }

    companion object {
        private val LOGGING_TAG = MainActivity::class.java.getSimpleName()
    }
}
