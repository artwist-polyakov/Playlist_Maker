package com.example.playlistmaker.settings.data

import com.example.playlistmaker.settings.models.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)

    fun openEmail(email: EmailData)
}