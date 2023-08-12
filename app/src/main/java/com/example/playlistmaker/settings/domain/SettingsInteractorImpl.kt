package com.example.playlistmaker.settings.domain

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.common.data.ThemeSettings
import com.example.playlistmaker.settings.data.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository,
    private val context: Context
) : SettingsInteractor {
    override fun getThemeSettings(): ThemeSettings {
        return settingsRepository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        settingsRepository.updateThemeSetting(settings)
    }

    override fun getShareLink(): String {
        return context.getString(R.string.share_link)
    }

    override fun getSupportEmail(): String {
        return context.getString(R.string.support_email)
    }

    override fun getAgreementLink(): String {
        return context.getString(R.string.agreement_link)
    }

    private fun getSupportSubject(): String {
        return context.getString(R.string.support_subject)
    }

    override fun getSupportEmailDate(): EmailData {
        return EmailData(getSupportEmail(),
            getSupportSubject(),
            ""
        )
    }
}
