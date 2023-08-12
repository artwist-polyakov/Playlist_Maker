package com.example.playlistmaker.common.domain.usecases

interface UseCaseInterface<T> {
    fun execute(engine: T? = null)
}