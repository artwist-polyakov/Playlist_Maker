package com.example.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.main.ui.view_model.MainViewModel
import com.example.playlistmaker.media.ui.activity.MediaActivity
import com.example.playlistmaker.search.ui.activity.SearchActivity
import com.example.playlistmaker.settings.ui.activity.SettingsActivity

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this, factory = MainViewModel.getViewModelFactory()).get(MainViewModel::class.java)
        findViewById<Button>(R.id.search).setOnClickListener {
            viewModel.onSearchClicked()
        }

        findViewById<Button>(R.id.media).setOnClickListener {
            viewModel.onMediaClicked()
        }

        findViewById<Button>(R.id.settings).setOnClickListener {
            viewModel.onSettingsClicked()
        }

        viewModel.navigationEvent.observe(this) { event ->
            val intent = when (event) {
                MainViewModel.NavigationEvent.SEARCH -> {
                    Intent(this, SearchActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                }
                MainViewModel.NavigationEvent.MEDIA -> {
                    Intent(this, MediaActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                }
                MainViewModel.NavigationEvent.SETTINGS -> {
                    Intent(this, SettingsActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                }
            }
            startActivity(intent)
        }

    }
}