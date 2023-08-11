package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.domain.EmailData
import com.example.playlistmaker.settings.domain.ExternalNavigator
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
        return ""
    }

    private fun getSupportEmailData(): EmailData {
        return EmailData("support_email", "support_subject", "support_text")
    }

    private fun getTermsLink(): String {
        return ""
    }
}