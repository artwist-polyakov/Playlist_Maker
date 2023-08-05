package com.example.playlistmaker.domain.common.usecases

interface UseCaseInterface<T> {
    fun execute(engine: T? = null)
}