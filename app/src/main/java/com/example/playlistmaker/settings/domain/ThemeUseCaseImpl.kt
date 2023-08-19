package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.common.domain.ThemeRepository

class ThemeUseCaseImpl(private val themeRepository: ThemeRepository) : ThemeUseCase {
    override fun isDarkTheme(): Boolean = themeRepository.isDarkTheme()
}