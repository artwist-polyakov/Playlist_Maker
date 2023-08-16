package com.example.playlistmaker.settings.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Observer
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val viewModel: SettingsViewModel by viewModel()
    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.returnButton.setOnClickListener {
            viewModel.onBackClicked()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onThemeSwitch(isChecked)
        }

        binding.sharingLayout.setOnClickListener {
            viewModel.shareLink()
        }

        binding.supportLayout.setOnClickListener {
            viewModel.sendSupport()
        }

        binding.agreementLayout.setOnClickListener {
            viewModel.openAgreement()
        }

        // Обработка событий от ViewModel
        viewModel.closeScreen.observe(this, Observer {
            finish()
        })


        viewModel.isDarkTheme.observe(this, Observer { isDark ->
            binding.themeSwitcher.isChecked = isDark
        })

        viewModel.themeSwitcherEnabled.observe(this, Observer { isEnabled ->
            binding.themeSwitcher.setEnabled(isEnabled)
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
}

