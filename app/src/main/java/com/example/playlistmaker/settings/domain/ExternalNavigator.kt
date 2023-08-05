package com.example.playlistmaker.settings.domain

import com.example.playlistmaker.common.domain.models.EmailData

interface ExternalNavigator {
    fun shareLink(link: String)
    fun openLink(link: String)

    fun openEmail(email: EmailData)
}