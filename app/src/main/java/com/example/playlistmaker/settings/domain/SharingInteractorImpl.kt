package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.settings.models.EmailData
import com.example.playlistmaker.settings.data.ExternalNavigator
import com.example.playlistmaker.settings.domain.SharingInteractor

class SharingInteractorImpl(
    private val externalNavigator: ExternalNavigator,
) : SharingInteractor {
    override fun shareApp() {
        externalNavigator.shareLink(getShareAppLink())
    }

    override fun openTerms() {
        externalNavigator.openLink(getTermsLink())
    }

    override fun openSupport() {
        externalNavigator.openEmail(getSupportEmailData())
    }

    private fun getShareAppLink(): String {
        // Нужно реализовать
        return ""
    }

    private fun getSupportEmailData(): EmailData {
        // Нужно реализовать
        return EmailData("support_email", "support_subject", "support_text")
    }

    private fun getTermsLink(): String {
        // Нужно реализовать
        return ""
    }
}