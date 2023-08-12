package com.example.playlistmaker.settings.domain

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)
    fun openEmail(email: EmailData)
}