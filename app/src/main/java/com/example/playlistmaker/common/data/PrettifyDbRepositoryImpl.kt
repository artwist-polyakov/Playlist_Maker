package com.example.playlistmaker.common.data

import com.example.playlistmaker.common.data.db.AppDatabase
import com.example.playlistmaker.common.domain.db.PrettifyDbRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class PrettifyDbRepositoryImpl(
    private val appDatabase: AppDatabase,
) : PrettifyDbRepository {
    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private var isRunning: Boolean = false

    init {
        launchPrettify()
    }

    override suspend fun startPrettify() {
        launchPrettify()
    }

    override suspend fun stopPrettify() {
        if (!isRunning) return // защитимся от многократных остановок
        isRunning = false
        /* плохая идея делать scope.cancel */
        job.cancelChildren() // так мы сможем перезапустить scope
    }

    private fun launchPrettify() {
        if (isRunning) return // защитимся от многократных запусков
        isRunning = true
        scope.launch {
            appDatabase
                .trackDao()
                .getGarbageTracks()
                .distinctUntilChanged()
                .collect {
                    it.forEach { track ->
                        appDatabase
                            .trackDao()
                            .deleteTrack(track)
                    }
                }
        }
    }
}
