package com.example.playlistmaker.settings.domain

class NavigationInteractor(private val navigator: ExternalNavigator) {

    fun navigateToSupport(email: EmailData) {
        navigator.openEmail(email)
    }

    fun navigateToAgreement(link: String) {
        navigator.openLink(link)
    }

    fun navigateToShare(link: String) {
        navigator.shareLink(link)
    }

    fun navigateToMessage(message: String) {
        navigator.sendMessage(message)
    }
}
