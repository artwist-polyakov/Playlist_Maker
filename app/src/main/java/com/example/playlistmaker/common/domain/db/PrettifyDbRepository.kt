package com.example.playlistmaker.common.domain.db

interface PrettifyDbRepository {
    suspend fun startPrettify()
    suspend fun stopPrettify()
}