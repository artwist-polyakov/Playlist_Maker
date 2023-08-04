package com.example.playlistmaker.ui.settings

import android.content.res.Configuration
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.content.Intent
import android.widget.LinearLayout
import com.example.playlistmaker.App
import com.example.playlistmaker.ui.mainscreen.MainActivity
import com.example.playlistmaker.R
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val backButton = findViewById<ImageView>(R.id.return_button)
        val sharingLayout = findViewById<LinearLayout>(R.id.sharing_layout)
        val supportLayout = findViewById<LinearLayout>(R.id.support_layout)
        val agreementLayout = findViewById<LinearLayout>(R.id.agreement_layout)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val sharingAction = {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            startActivity(intent)
        }
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        themeSwitcher.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as App).switchTheme(checked)

            with(getSharedPreferences(MainActivity.PREFS, MODE_PRIVATE).edit()) {
                putBoolean(MainActivity.THEME_PREF, checked)
                apply()
            }
        }

        backButton.setOnClickListener {
//            val intent = Intent(this@SettingsActivity, MainActivity::class.java)
//            startActivity(intent)
            this.finish()
        }
        sharingLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_link))
            }
            startActivity(intent)
        }

        supportLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, getString(R.string.support_email))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.support_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.support_text))
            }
            startActivity(intent)
        }

        agreementLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(getString(R.string.agreement_link))
            }
            startActivity(intent)
        }
    }

}
