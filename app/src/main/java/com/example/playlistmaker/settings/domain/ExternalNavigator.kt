package com.example.playlistmaker.settings.domain

import android.content.Intent

interface ExternalNavigator {
    fun shareLink(link: String): Intent
    fun openLink(link: String): Intent
    fun openEmail(email: EmailData): Intent
}