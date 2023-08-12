package com.example.playlistmaker.settings.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.playlistmaker.settings.domain.ExternalNavigator
import com.example.playlistmaker.settings.domain.EmailData

class ExternalNavigatorImpl(private val context: Context) : ExternalNavigator {

    override fun shareLink(link: String) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, link)
            type = "text/plain"
        }
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }

    override fun openLink(link: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(browserIntent)
    }

    override fun openEmail(email: EmailData) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email.email))
            putExtra(Intent.EXTRA_SUBJECT, email.subject)
            putExtra(Intent.EXTRA_TEXT, email.message)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
