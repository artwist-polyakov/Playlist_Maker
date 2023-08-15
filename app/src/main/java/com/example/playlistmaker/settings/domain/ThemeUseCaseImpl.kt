package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.common.domain.api.ThemeRepository

class ThemeUseCaseImpl(private val themeRepository: ThemeRepository) : ThemeUseCase {
    override fun isDarkTheme(): Boolean = themeRepository.isDarkTheme()
}