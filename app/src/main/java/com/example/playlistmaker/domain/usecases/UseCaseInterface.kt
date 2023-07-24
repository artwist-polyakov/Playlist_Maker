package com.example.playlistmaker.domain.usecases

interface UseCaseInterface<T> {
    fun execute(engine: T? = null)
}