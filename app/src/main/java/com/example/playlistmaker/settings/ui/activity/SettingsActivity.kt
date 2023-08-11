package com.example.playlistmaker.settings.ui.activity

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.LinearLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.common.data.ThemeRepository
import com.example.playlistmaker.common.data.ThemeRepositoryImpl
import com.example.playlistmaker.settings.data.ExternalNavigatorImpl
import com.example.playlistmaker.settings.domain.SettingsInteractorImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val themeRepository = ThemeRepositoryImpl(applicationContext)
        val settingsRepository = SettingsRepositoryImpl(themeRepository)
        val settingsInteractor = SettingsInteractorImpl(settingsRepository, this)
        val externalNavigator = ExternalNavigatorImpl(this)
        val factory = SettingsViewModel.getViewModelFactory(settingsInteractor, externalNavigator)
        viewModel = ViewModelProvider(this, factory).get(SettingsViewModel::class.java)


        val backButton = findViewById<ImageView>(R.id.return_button)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)
        val sharingLayout = findViewById<LinearLayout>(R.id.sharing_layout)
        val supportLayout = findViewById<LinearLayout>(R.id.support_layout)
        val agreementLayout = findViewById<LinearLayout>(R.id.agreement_layout)

        backButton.setOnClickListener {
            viewModel.onBackClicked()
        }

        themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitch(isChecked)
        }

        sharingLayout.setOnClickListener {
            viewModel.onShareClicked()
        }

        supportLayout.setOnClickListener {
            viewModel.onSupportClicked()
        }

        agreementLayout.setOnClickListener {
            viewModel.onAgreementClicked()
        }

        // Обработка событий от ViewModel
        viewModel.closeScreen.observe(this, Observer {
            finish()
        })

        viewModel.shareLink.observe(this, Observer { link ->
            externalNavigator.shareLink(link)
        })

        viewModel.openLink.observe(this, Observer { link ->

            externalNavigator.openLink(link)
        })

        viewModel.sendEmail.observe(this, Observer { email ->
            externalNavigator.openEmail(email)
        })

        viewModel.isDarkTheme.observe(this, Observer { isDark ->
            themeSwitcher.isChecked = isDark
        })

        viewModel.themeSwitcherEnabled.observe(this, Observer { isEnabled ->
            themeSwitcher.setEnabled(isEnabled)
        })

        viewModel.restartActivity.observe(this) {
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                overridePendingTransition(0, 0)
                finish()
            }, 500)  // Задержка в 0.5 секунды
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // Вы можете добавить здесь дополнительный код, если это необходимо.
    }
}

