package com.example.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.App
import com.example.playlistmaker.R
import com.example.playlistmaker.common.data.ThemeRepository
import com.example.playlistmaker.main.ui.view_model.MainViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val themeRepository = ThemeRepository(applicationContext)
        val factory = MainViewModel.getViewModelFactory(application, themeRepository)
        viewModel = ViewModelProvider(this, factory).get(MainViewModel::class.java)

        findViewById<Button>(R.id.search).setOnClickListener {
            viewModel.onSearchClicked()
        }

        findViewById<Button>(R.id.media).setOnClickListener {
            viewModel.onMediaClicked()
        }

        findViewById<Button>(R.id.settings).setOnClickListener {
            viewModel.onSettingsClicked()
        }

        viewModel.navigateTo.observe(this) { intent ->
            startActivity(intent)
        }

    }
}