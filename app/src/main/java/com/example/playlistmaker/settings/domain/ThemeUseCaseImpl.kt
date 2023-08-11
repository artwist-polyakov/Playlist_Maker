package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.common.data.ThemeRepository
import com.example.playlistmaker.main.domain.ThemeUseCase

class ThemeUseCaseImpl(private val themeRepository: ThemeRepository) : ThemeUseCase {
    override fun isDarkTheme(): Boolean = themeRepository.isDarkTheme()
}