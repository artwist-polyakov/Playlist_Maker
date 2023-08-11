package com.example.playlistmaker.settings.data

import com.example.playlistmaker.common.domain.api.ThemeRepository
import com.example.playlistmaker.main.domain.ThemeUseCase

class ThemeUseCaseImpl(private val themeRepository: ThemeRepository) : ThemeUseCase {
    override fun isDarkTheme(): Boolean = themeRepository.isDarkTheme()
}